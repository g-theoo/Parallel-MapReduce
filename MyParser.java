import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MyParser {
	/**
	 * Class that parse the input file
	 */
	private final String file;
	private int size = 0;

	public MyParser(String file) {
		this.file = file;
	}

	public int getSize() {
		return size;
	}

	/**
	 * Parse and compute input for Map tasks
	 * @return necessary input for Map tasks
	 * @throws IOException
	 */
	public ArrayList<MapData> parse() throws IOException {
		ArrayList<MapData> tasks = new ArrayList<>();
		ArrayList<String> documents = new ArrayList<>();

		File file = new File(this.file);
		BufferedReader br = new BufferedReader(new FileReader(file));

		size = Integer.parseInt((br.readLine()));
		int numberOfFiles = Integer.parseInt(br.readLine());

		for(int i = 0; i < numberOfFiles; i++) {
			documents.add(br.readLine());
		}

		for(String document : documents) {
			File documentFile = new File(document);
			int documentLength = (int) documentFile.length();

			int offset = 0;

			while(true) {
				if(offset + size > documentLength) {
					tasks.add(new MapData(document, offset, documentLength - offset ));
					break;
				} else {
					tasks.add(new MapData(document, offset, size));
					offset += size;
				}
			}
		}

		return tasks;
	}
}
