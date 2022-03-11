import java.util.ArrayList;
import java.util.HashMap;

public class OutDataMap {
	/**
	 * Class that store the output data from Map tasks
	 */
	private final String document;
	private final HashMap<Integer, Integer> dictionary;
	private final ArrayList<String> longestWords;


	public OutDataMap(String document, HashMap<Integer, Integer> dictionary, ArrayList<String> longestWords) {
		this.document = document;
		this.dictionary = dictionary;
		this.longestWords = longestWords;
	}

	public String getDocument() {
		return document;
	}

	public HashMap<Integer, Integer> getDictionary() {
		return dictionary;
	}

	public ArrayList<String> getLongestWords() {
		return longestWords;
	}

	@Override
	public String toString() {

		return document + " " +
				dictionary + " " +
				longestWords + "\n";

	}
}
