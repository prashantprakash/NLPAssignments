import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class DataSource {

	public void readCorpusFromFile(String path, Map<String, Integer> vocabulary) {
		try {
			FileReader input = new FileReader(path);
			BufferedReader br = new BufferedReader(input);
			String line = br.readLine();
			while (line != null) {
				StringTokenizer token = new StringTokenizer(line, " ");
				while (token.hasMoreTokens()) {
					String word = token.nextToken().trim().toLowerCase();
					if (word.length() == 1 && !word.equals(".")) {
						word = word.replaceAll("[^a-zA-Z0-9]+", "");
					}
					if (word.length() == 0)
						continue;
					if (vocabulary.containsKey(word)) {
						int value = vocabulary.get(word);
						int newValue = value + Constant.ONE;
						vocabulary.put(word, newValue);
					} else {
						vocabulary.put(word, Constant.ONE);
					}
				}
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			System.out.println("Error in reading corpus File");
		}
	}

	public List<String> readInputFile(String inputPath) {
		List<String> sentenceList = new ArrayList<String>();
		try {

			FileReader reader = new FileReader(inputPath);
			BufferedReader br = new BufferedReader(reader);
			String sentence = br.readLine();
			sentenceList.add(sentence);

		} catch (Exception ex) {
			System.err.println();
		}
		return sentenceList;

	}
}
