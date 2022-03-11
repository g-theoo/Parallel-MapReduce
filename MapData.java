public class MapData {
	/**
	 * Class that store the necessary data for a Map Task
	 */
	private final String document;
	private final int offset;
	private final int dimension;

	public MapData(String document, int offset, int dimension) {
		this.document = document;
		this.offset = offset;
		this.dimension = dimension;
	}

	public String getDocument() {
		return document;
	}

	public int getOffset() {
		return offset;
	}

	public int getDimension() {
		return dimension;
	}

	@Override
	public String toString() {
		return document + " " +  offset + " " +  dimension + "\n";
	}
}
