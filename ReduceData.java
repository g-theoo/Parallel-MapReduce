import java.util.ArrayList;
import java.util.HashMap;

public class ReduceData {
	/**
	 * Class that store the necessary data for Reduce task
	 */
	private final HashMap<Integer, Integer> dictionary;
	private final ArrayList<String> longestWords;

	public ReduceData(HashMap<Integer, Integer> dictionary, ArrayList<String> longestWords) {
		this.dictionary = dictionary;
		this.longestWords = longestWords;
	}

	public HashMap<Integer, Integer> getDictionary() {
		return dictionary;
	}

	public ArrayList<String> getLongestWords() {
		return longestWords;
	}

	@Override
	public String toString() {
		return dictionary + " " + longestWords + "\n";
	}
}
