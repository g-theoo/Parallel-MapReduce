import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class ReduceRun implements Runnable{
	/**
	 * Class that execute Reduce tasks
	 */
	private final String document;
	private final ReduceData data;
	private final ExecutorService tpe;
	private final AtomicInteger inQueue;
	private final List<ReduceOutData> reduceOutData;
	private final CompletableFuture<List<ReduceOutData>> completableFuture;

	public ReduceRun(String document, ReduceData data, ExecutorService tpe, AtomicInteger inQueue,
	                 List<ReduceOutData> reduceOutData, CompletableFuture<List<ReduceOutData>> completableFuture) {
		this.document = document;
		this.data = data;
		this.tpe = tpe;
		this.inQueue = inQueue;
		this.completableFuture = completableFuture;
		this.reduceOutData = reduceOutData;
	}


	/**
	 * Execute a Reduce task
	 */
	@Override
	public void run() {

		HashMap<Integer, Integer> dictionary = data.getDictionary();
		ArrayList<String> longestWords = data.getLongestWords();

		int sum = 0;
		float numberOfWords = 0;
		float rang = 0;
		int longest = 0;
		int numberOfAppearances = 0;

		// Compute the data to obtain "rang"
		for(Map.Entry<Integer, Integer> entry : dictionary.entrySet()) {
			if(entry.getKey() != 0) {
				sum += Fibonacci.fib(entry.getKey() + 1) * entry.getValue();
				numberOfWords += entry.getValue();
			}
		}

		// Compute "rang"
		if(numberOfWords > 0) {
			rang = sum / numberOfWords;
		} else {
			rang = 0;
		}

		// Get longest length
		for(String word : longestWords) {
			if(longest < word.length()) {
				longest = word.length();
			}
		}

		// Compute number of appearances
		for(String word : longestWords) {
			if(word.length() == longest) {
				numberOfAppearances ++;
			}
		}

		reduceOutData.add(new ReduceOutData(document, rang, longest, numberOfAppearances));

		int left = inQueue.decrementAndGet();
		if(left == 0) {
			completableFuture.complete(reduceOutData);
			tpe.shutdown();
		}
	}
}
