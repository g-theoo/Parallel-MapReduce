public class ReduceOutData implements Comparable<ReduceOutData> {
	/**
	 * Class that store the output data from Reduce tasks
	 */
	private final String document;
	private final float rang;
	private final int longest;
	private final int numberOfAppearances;

	public String getDocument() {
		return document;
	}

	public float getRang() {
		return rang;
	}

	public int getLongest() {
		return longest;
	}

	public int getNumberOfAppearances() {
		return numberOfAppearances;
	}

	public ReduceOutData(String document, float rang, int longest, int numberOfAppearances) {
		this.document = document;
		this.rang = rang;
		this.longest = longest;
		this.numberOfAppearances = numberOfAppearances;
	}

	@Override
	public String toString() {
		return document + "," + String.format("%.2f", rang) + "," + longest + "," + numberOfAppearances;
	}

	@Override
	public int compareTo(ReduceOutData o) {
		return Float.compare(o.getRang(), rang);
	}
}

