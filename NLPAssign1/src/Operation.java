import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Operation {
	public Map<String, ColumnData> bigramCountTable = new HashMap<String, ColumnData>();
	public Map<String, Integer> vocabulary = new HashMap<String, Integer>();
	public Map<Integer, Integer> bigramCountMap = new HashMap<Integer, Integer>();
	public int bigramCount;
	Map<String, ColumnData> sentenceBigramCountTable = new HashMap<String, ColumnData>();

	public Operation(Map<String, ColumnData> bigramCountTable, Map<String, Integer> vocabulary,
			Map<Integer, Integer> bigramCountMap, int bigramCount) {
		// TODO Auto-generated constructor stub
		this.bigramCountTable = bigramCountTable;
		this.vocabulary = vocabulary;
		this.bigramCountMap = bigramCountMap;
		this.bigramCount = bigramCount;
	}

	public double getBigramProbability(String word, String prevWord) {
		double v = Constant.ZERO;
		if (bigramCountTable.containsKey(word)) {
			if (bigramCountTable.get(word).columnValues.containsKey(prevWord))
				v = bigramCountTable.get(word).columnValues.get(prevWord);
		}
		if (v > 0) {
			double prob = (double) v / vocabulary.get(prevWord);
			return prob;
		}
		return Constant.ZERO;
	}

	public double getBigramSmoothingProbability(String word, String prevWord) {
		double v = Constant.ZERO;
		double prob = Constant.ZERO;
		if (bigramCountTable.containsKey(word)) {
			if (bigramCountTable.get(word).columnValues.containsKey(prevWord))
				v = bigramCountTable.get(word).columnValues.get(prevWord);
		} else {
			v = 1.0; // smoothing
		}
		if (vocabulary.get(prevWord) != null) {
			prob = (double) (v + Constant.ONE) / (vocabulary.get(prevWord) + vocabulary.size());
		} else {
			prob = (double) (v + Constant.ONE) / (Constant.ONE + vocabulary.size());
		}

		return prob;
	}

	public double getBigramGoodTuringProbability(String word, String prevWord) {
		double cD = Constant.ZERO;
		if (bigramCountTable.containsKey(word)) {
			if (bigramCountTable.get(word).columnValues.containsKey(prevWord))
				cD = bigramCountTable.get(word).columnValues.get(prevWord);
		}
		int c = (int) cD;
		if (bigramCountMap.get(c + Constant.ONE) != null) {
			double cStar = (double) (c + Constant.ONE) * bigramCountMap.get(c + Constant.ONE) / bigramCountMap.get(c);
			return (double) cStar / bigramCount;
		}
		return Constant.ZERO;
	}

	public Map<String, ColumnData> constructBigramProbabilitiesTable(String sentence, String probType) {
		Map<String, ColumnData> sentenceBigramCountTable = new HashMap<>();
		String[] segments = sentence.toLowerCase().split(" ");
		Set<String> minimizedSet = new HashSet<String>(Arrays.asList(segments));
		for (int i = 0; i < segments.length; i++) {
			ColumnData d = new ColumnData(minimizedSet);
			for (int j = 0; j < segments.length; j++) {
				if (probType.equalsIgnoreCase(Constant.GOODTURINGSMOTHING)) {
					d.columnValues.put(segments[j], getBigramGoodTuringProbability(segments[i], segments[j]));
				} else if (probType.equalsIgnoreCase(Constant.ONESMOTHING)) {
					d.columnValues.put(segments[j], getBigramSmoothingProbability(segments[i], segments[j]));
				} else if (probType.equalsIgnoreCase(Constant.NOSMOTHING)) {
					d.columnValues.put(segments[j], getBigramProbability(segments[i], segments[j]));
				}
			}
			sentenceBigramCountTable.put(segments[i].trim().toLowerCase(), d);
		}

		return sentenceBigramCountTable;
	}

	public double calculateTotalProbability(String sentence, String probType) {
		double totalProb = 1;
		String[] segments = sentence.toLowerCase().split(" ");
		for (int i = 0; i < segments.length; i++) {
			double p =0;
			if (i == 0) {
				if (probType.equalsIgnoreCase(Constant.GOODTURINGSMOTHING)) {
					p = getBigramGoodTuringProbability(segments[i], ".");
				} else if (probType.equalsIgnoreCase(Constant.ONESMOTHING)) {
					p = getBigramSmoothingProbability(segments[i], ".");
				} else if (probType.equalsIgnoreCase(Constant.NOSMOTHING)) {
					p = getBigramProbability(segments[i], ".");
				}

				if (p > 0)
					totalProb = totalProb * p;
			} else {
				if (probType.equalsIgnoreCase(Constant.GOODTURINGSMOTHING)) {
					p = getBigramGoodTuringProbability(segments[i], segments[i - 1]);
				} else if (probType.equalsIgnoreCase(Constant.ONESMOTHING)) {
					p = getBigramSmoothingProbability(segments[i], segments[i - 1]);
				} else if (probType.equalsIgnoreCase(Constant.NOSMOTHING)) {
					p = getBigramProbability(segments[i], segments[i - 1]);
				}

				if (p > 0)
					totalProb = totalProb * p;
			}
		}
		return totalProb;
	}

}
