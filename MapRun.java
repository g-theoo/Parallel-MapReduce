import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class MapRun implements Runnable{
	/**
	 * Class that execute Map tasks
	 */
	private final MapData task;
	private final ExecutorService tpe;
	private final AtomicInteger inQueue;
	private final List<OutDataMap> outData;
	private final CompletableFuture<List<OutDataMap>> completableFuture;

	public MapRun(MapData task, ExecutorService tpe, AtomicInteger inQueue, List<OutDataMap> outData, CompletableFuture<List<OutDataMap>> completableFuture) {
		this.task = task;
		this.tpe = tpe;
		this.inQueue = inQueue;
		this.outData = outData;
		this.completableFuture = completableFuture;
	}

	/**
	 * Execute a Map task
	 */
	@Override
	public void run() {
		File file = new File(task.getDocument());
		String documentContent = null;
		String workerContent = null;
		String[] workerSplittedContent = new String[0];
		HashMap<Integer, Integer> words = new HashMap<>();
		ArrayList<String> longestWords = new ArrayList<>();

		try {
			documentContent = Files.readString(Paths.get(task.getDocument()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (documentContent != null) {
			workerContent = documentContent.substring(task.getOffset(), task.getOffset() + task.getDimension());
		}

		if (workerContent != null) {
			workerSplittedContent = workerContent.split("[^A-Za-z0-9]+");
		}

		char lastChar = workerContent.charAt(workerContent.length() - 1);

		// Last character is a symbol
		if(Character.toString(lastChar).matches("[^A-Za-z0-9]+")) {
			if (task.getOffset() != 0) {
				// Verify the first word
				char previousChar = documentContent.charAt(task.getOffset() - 1);
				if (Character.toString(previousChar).matches("[A-Za-z0-9]+")) {
					// It's not a complete word -> Remove it
					workerSplittedContent[0] = null;
				}
			}
		}
		// Last character is a valid
		if(Character.toString(lastChar).matches("[A-Za-z0-9]+")) {
			if(task.getOffset() == 0) {
				// Verifies only the last word
				char nextChar = documentContent.charAt(task.getOffset() + task.getDimension());
				if(!Character.toString(nextChar).matches("[^A-Za-z0-9]+")) {
					// Compute the complete word if the next character is a valid character
					String nextContent = documentContent.substring(task.getOffset() + task.getDimension());
					String[] finalPart = nextContent.split("[^A-Za-z0-9]+");
					String lastWord = workerSplittedContent[workerSplittedContent.length - 1].concat(finalPart[0]);
					workerSplittedContent[workerSplittedContent.length - 1] = lastWord;
				}
			} else if(task.getOffset() + task.getDimension() == documentContent.length()) {
				// Verifies only the first word
				char previousChar = documentContent.charAt(task.getOffset() - 1);
				if (Character.toString(previousChar).matches("[A-Za-z0-9]+")) {
					// It's not a complete word -> Remove it
					workerSplittedContent[0] = null;
				}
			} else {
				// Verifies the both, first and last words

				// Verifies the last word
				char nextChar = documentContent.charAt(task.getOffset() + task.getDimension());
				if(Character.toString(nextChar).matches("[A-Za-z0-9]+")) {
					// Compute the complete word if the next character is a valid character
					String nextContent = documentContent.substring(task.getOffset() + task.getDimension());
					String[] finalPart = nextContent.split("[^A-Za-z0-9]+");
					String lastWord = workerSplittedContent[workerSplittedContent.length - 1].concat(finalPart[0]);
					workerSplittedContent[workerSplittedContent.length - 1] = lastWord;
				}

				// Verifies the first word
				char previousChar = documentContent.charAt(task.getOffset() - 1);
				if (Character.toString(previousChar).matches("[A-Za-z0-9]+")) {
					// It's not a complete word -> Remove it
					workerSplittedContent[0] = null;
				}
			}
		}

		// Compute the dictionary and longest length
		int longest = 0;
		for(String content : workerSplittedContent) {
			if(content != null) {
				if(words.containsKey(content.length())) {
					words.replace(content.length(), words.get(content.length()) + 1 );
				} else {
					words.put(content.length(), 1);
				}

				if(longest < content.length()) {
					longest = content.length();
				}
			}
		}

		// Extract all the longest words
		for(String content : workerSplittedContent) {
			if(content != null) {
				if (longest == content.length()) {
					longestWords.add(content);
				}
			}
		}

		outData.add(new OutDataMap(task.getDocument(), words, longestWords));

		int left = inQueue.decrementAndGet();
		if(left == 0) {
			completableFuture.complete(outData);
			tpe.shutdown();
		}
	}

}
