import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/*
 * Author : Prashant Prakash
 * Description : This class builds the bigram for the given corpus 
 * Created_Date : 22nd Sept 2015
 */
public class Bigram {
	public Map<String, ColumnData> bigramCountTable = new HashMap<String, ColumnData>();
	public int bigramCount;
	public Map<Integer, Integer> bigramCountMap = new HashMap<Integer, Integer>();

	public Bigram(Set<String> vocab) {
		// Initialize the table
		for (String rowtoken : vocab) {
			Set<String> coldata = new HashSet<String>();
			for (String coltoken : vocab) {
				coldata.add(coltoken);
			}
			bigramCountTable.put(rowtoken, new ColumnData(coldata));
		}
	}

	public void buildBigram(String corpusPath) {
		String previousWord = Constant.SENTENCETOKEN;
		try {
			FileReader input = new FileReader(corpusPath);
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
					if (!previousWord.equals("")) {
						int count = bigramCountTable.get(previousWord).columnValues.get(word).intValue() + Constant.ONE;
						bigramCountTable.get(previousWord).columnValues.put(word, (double) count);
						previousWord = word;
						bigramCount++;
					}

				}
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			System.out.println("Error in reading corpus File");
		}

	}

	public void buildBigramCountMap() {
		for (String key : bigramCountTable.keySet()) {
			for (String prev : bigramCountTable.get(key).columnValues.keySet()) {
				double dVal = bigramCountTable.get(key).columnValues.get(prev);
				int val = (int) dVal;
				if (bigramCountMap.get(val) == null) {
					bigramCountMap.put(val, Constant.ONE);
				} else {
					int oldVal = bigramCountMap.get(val);
					bigramCountMap.put(val, oldVal + Constant.ONE);
				}
			}
		}
	}

	public Map<String, ColumnData> constructBigramCountTable(String sentence) {
		Map<String, ColumnData> sentenceBigramProbTable = new HashMap<>();
		String[] segments = sentence.toLowerCase().split(" ");
		Set<String> minimizedSet = new HashSet<String>(Arrays.asList(segments));
		for (int i = 0; i < segments.length; i++) {
			ColumnData d = new ColumnData(minimizedSet);
			for (int j = 0; j < segments.length; j++) {
				if (bigramCountTable.containsKey(segments[i])) {
					if (bigramCountTable.get(segments[i]).columnValues.containsKey(segments[j])) {
						d.columnValues
								.put(segments[j], bigramCountTable.get(segments[i]).columnValues.get(segments[j]));
					} else {
						d.columnValues.put(segments[j], Constant.ZERO);
					}
				}
			}
			sentenceBigramProbTable.put(segments[i].trim().toLowerCase(), d);
		}
		return sentenceBigramProbTable;
	}

}
