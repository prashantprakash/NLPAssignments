import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author PRASHANT PRAKASH Class Description : This class handles Bigram
 *         implementation of given corpus using various techniques Created
 *         _date: 22nd sept 2015 Version : Initial Version
 */

public class BigramImpl {
	public static void main(String[] args) {
		// capture all input parameters
		String corpusFile = args[0];
		String inputFile = args[1];
		// Vocabulary contains all tokens with the count it has appeared in the
		// corpus
		Map<String, Integer> vocabulary = new HashMap<String, Integer>();
		// read the corpus and build the Vocabulary
		DataSource dataSource = new DataSource();
		dataSource.readCorpusFromFile(corpusFile, vocabulary);
		System.out.println("Size of vocab is:  " + vocabulary.size());
		Bigram bigram = new Bigram(vocabulary.keySet());
		bigram.buildBigram(corpusFile);
		Operation operation = new Operation(bigram.bigramCountTable, vocabulary, bigram.bigramCountMap,
				bigram.bigramCount);

		List<String> sentenceList = dataSource.readInputFile(inputFile);
		Result result = new Result();
		for (int i = 0; i < sentenceList.size(); i++) {
			constructBigramProbabilitytable(sentenceList.get(i), operation, bigram);
			calculateTotalProbabilty(sentenceList.get(i), operation, result);
		}
		printFinalResults(result);
	}

	public static void printFinalResults(Result result) {
		System.out.println("\n\n***********************");
		System.out.println("Maximum Probabilities");
		System.out.println("***********************");
		System.out.println("Without Smoothing: " + result.getMaxprobWithoutSmoothing());
		System.out.println("With Smoothing: " + result.getMaxprobWithSmoothing());
		System.out.println("Good Turing : " + result.getMaxprobGoodTuring());

		System.out.println("\n********************");
		System.out.println("Probable Sentences");
		System.out.println("********************");
		System.out.println("Without Smoothing: " + result.getOutSentenceWithoutSmoothing());
		System.out.println("With Smoothing: " + result.getOutSentenceWithSmoothing());
		System.out.println("Good Turing : " + result.getOutSentenceGoodTurning());

	}

	public static void calculateTotalProbabilty(String sentence, Operation operation, Result result) {

		result.setProbWithoutSmoothing(operation.calculateTotalProbability(sentence, Constant.NOSMOTHING));
		result.setProbWithSmoothing(operation.calculateTotalProbability(sentence, Constant.ONESMOTHING));
		result.setProbGoodTuring(operation.calculateTotalProbability(sentence, Constant.GOODTURINGSMOTHING));

		System.out.println("\n\n\t**********************************************");
		System.out.println("\tProbability of Sentence (Without Smoothing): " + result.getProbWithoutSmoothing());
		System.out.println("\tProbability of Sentence (With Smoothing): " + result.getProbWithSmoothing());
		System.out.println("\tProbability of Sentence (Good Turing): " + result.getProbGoodTuring());
		System.out.println("\t**********************************************");
		if (result.getProbWithoutSmoothing() > result.getMaxprobWithoutSmoothing()) {
			result.setMaxprobWithoutSmoothing(result.getProbWithoutSmoothing());
			result.setOutSentenceWithoutSmoothing(sentence);
		}
		if (result.getProbWithSmoothing() > result.getMaxprobWithSmoothing()) {
			result.setMaxprobWithSmoothing(result.getMaxprobWithSmoothing());
			result.setOutSentenceWithSmoothing(sentence);
		}
		if (result.getProbGoodTuring() > result.getMaxprobGoodTuring()) {
			result.setMaxprobGoodTuring(result.getProbGoodTuring());
			result.setOutSentenceGoodTurning(sentence);
		}

		System.out.println("\n\n");

	}

	public static void constructBigramProbabilitytable(String sentence, Operation operation, Bigram bigram) {
		BigramUtil bigramUtil = new BigramUtil();

		System.out.println("\n\tSENTENCE ");
		System.out.println("********************");
		System.out.println("Bigram Count Table");
		System.out.println("********************");
		bigramUtil.printTable(bigram.constructBigramCountTable(sentence));
		System.out.println("**************************************");
		System.out.println("Probabilities Table(Without Smoothing)");
		System.out.println("**************************************");
		bigramUtil.printTable(operation.constructBigramProbabilitiesTable(sentence, Constant.NOSMOTHING));

		System.out.println("\n**************************************");
		System.out.println("Probabilities Table(With Smoothing)");
		System.out.println("**************************************");
		bigramUtil.printTable(operation.constructBigramProbabilitiesTable(sentence, Constant.ONESMOTHING));

		System.out.println("\n**************************************");
		System.out.println("Probabilities Table(Good Turing)");
		System.out.println("**************************************");
		System.out.println("****Bigram Good Turing Probabilities Table - Sentence 1****");
		bigramUtil.printTable(operation.constructBigramProbabilitiesTable(sentence, Constant.GOODTURINGSMOTHING));

	}
}
