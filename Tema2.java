import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Tema2 {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }

        // number of workers
        int workers = Integer.parseInt(args[0]);
        // parse the input
        MyParser parser = new MyParser(args[1]);

        // data for Map phase
        ArrayList<MapData> mapData = parser.parse();
        // data for Reduce phase
        HashMap<String, ReduceData> reduceData = new HashMap<>();

        // output data from Map and Reduce
        List<OutDataMap> mapDataOut = Collections.synchronizedList(new ArrayList<>());
        List<ReduceOutData> reduceDataOut = Collections.synchronizedList(new ArrayList<>());

        // create the Map Pool
        AtomicInteger inQueue = new AtomicInteger(0);
        ExecutorService tpe = Executors.newFixedThreadPool(workers);

        // CompleableFuture for Map and Reduce
        CompletableFuture<List<OutDataMap>> completableFutureMap = new CompletableFuture<>();
        CompletableFuture<List<ReduceOutData>> completableFutureReduce = new CompletableFuture<>();

        // create the tasks for Map
        for(MapData task : mapData) {
            inQueue.incrementAndGet();
            tpe.submit(new MapRun(task, tpe, inQueue,mapDataOut, completableFutureMap));
        }

        // receive the output from Map phase
        mapDataOut = completableFutureMap.get();


        // compute Reduce input from Map output
        for(OutDataMap data : mapDataOut) {
            String document = data.getDocument();
            HashMap<Integer, Integer> dictionary = data.getDictionary();
            ArrayList<String> longestWords = data.getLongestWords();

            if(reduceData.containsKey(data.getDocument())) {
                ReduceData current = reduceData.get(document);
                HashMap<Integer, Integer> currentDictionary = current.getDictionary();
                ArrayList<String> currentLongestWords = current.getLongestWords();
                for(Map.Entry<Integer, Integer> entry : dictionary.entrySet()) {
                    if(currentDictionary.containsKey(entry.getKey())) {
                        currentDictionary.replace(entry.getKey(), currentDictionary.get(entry.getKey()) + entry.getValue());
                    } else {
                        currentDictionary.put(entry.getKey(), entry.getValue());
                    }
                }
                currentLongestWords.addAll(longestWords);

                reduceData.replace(document, new ReduceData(currentDictionary, currentLongestWords));
            } else {
                reduceData.put(document, new ReduceData(dictionary, longestWords));
            }
        }

        // create the Reduce Pool
        inQueue = new AtomicInteger(0);
        tpe = Executors.newFixedThreadPool(workers);

        // create the tasks for Reduce
        for(Map.Entry<String, ReduceData> data : reduceData.entrySet()) {
            inQueue.incrementAndGet();
            tpe.submit(new ReduceRun(data.getKey(), data.getValue(), tpe, inQueue, reduceDataOut, completableFutureReduce));
        }

        // receive the data from Reduce
        reduceDataOut = completableFutureReduce.get();

        // sort the output from Reduce by "rang"
        Collections.sort(reduceDataOut);

        BufferedWriter wr = new BufferedWriter(new FileWriter(args[2]));

        // write the final output in file
        for(int i = 0 ; i < reduceDataOut.size(); i ++) {
            String[] path = reduceDataOut.get(i).getDocument().split("/");
            String output = path[path.length - 1] + "," +
                            String.format("%.2f", reduceDataOut.get(i).getRang()) + "," +
                            reduceDataOut.get(i).getLongest() + "," + reduceDataOut.get(i).getNumberOfAppearances();
            wr.write(output + "\n");
        }
        wr.close();
    }
}
