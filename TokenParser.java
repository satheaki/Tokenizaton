import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Program for tokenizing and stemming words from Cranfield Database
 * 
 * @author Akshay
 *
 */
public class TokenParser {
	private static int phase = 0;
	private static long startTime, endTime, elapsedTime;
	private static int totalDocuments = 0;
	private static int totalTokens = 0;
	private static int totalUniqueTokens = 0;
	private static int singleCountTokens = 0;
	private static int totalStemmedTokens = 0;
	private static int totalUniqueStems = 0;
	private static int singleCountStems = 0;

	/* HashMap containing tokens as key with their frequency as value */
	private static HashMap<String, Integer> tokenMap = new HashMap<String, Integer>();

	/* TreeMap sorted on frequency */
	private static TreeMap<String, Integer> frequentTokenMap = new TreeMap<String, Integer>();

	/* HashMap for storing stems along with their frequency */
	private static HashMap<String, Integer> stemmedTokenMap = new HashMap<String, Integer>();
	private static TreeMap<String, Integer> frequentStemMap = new TreeMap<String, Integer>();

	/**
	 * Main Method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String dataDirectory = "";
		if (args.length == 0) {
			System.out.println("Folder path not specified!");
			return;
		} else {
			//dataDirectory = args[0];
			 dataDirectory =
			 "C:\\My files\\Information Retrieval\\Test Cran";
		
			timer();
			readDirectory(dataDirectory);
			timer();
		}

		iterateMap();
		frequentTokenMap = sortTokensOnFrequency(tokenMap);
		displayTokenData();
		displayStemmedData();
	}
	
	
	
	/**
	 * Method for iterating token and stem maps.
	 */
	private static void iterateMap() {
		for (Map.Entry<String, Integer> term : tokenMap.entrySet()) {
			totalTokens += term.getValue();
			totalUniqueTokens++;
			if (term.getValue() == 1) {
				singleCountTokens++;
			}
		}

		for (Map.Entry<String, Integer> stemmedToken : stemmedTokenMap
				.entrySet()) {
			totalStemmedTokens += stemmedToken.getValue();
			totalUniqueStems++;
			if (stemmedToken.getValue() == 1) {
				singleCountStems++;
			}
		}

	}

	/**
	 * Method for displaying the stemmed data output
	 */
	private static void displayStemmedData() {
		int topFrequentStemCounter = 30;
		System.out.println("\n---------------------------------");
		System.out.println("\n\t\tResult for Stems");
		System.out.println("\n---------------------------------");
		System.out.println("\nDistinct stems in cranfield database:"
				+ totalUniqueStems);
		System.out.println("\nSingle occurrence stems in cranfoeld database:"
				+ singleCountStems);

		System.out.println("\n30 most frequent stems:\n");
		frequentStemMap = sortTokensOnFrequency(stemmedTokenMap);

		System.out.println("Token\tFrequency");
		for (Map.Entry<String, Integer> topStem : frequentStemMap.entrySet()) {
			System.out.println(topStem.getKey() + ":  " + topStem.getValue());
			if (topFrequentStemCounter == 1)
				break;
			topFrequentStemCounter--;
		}

		System.out
				.println("\nAverage word stems per document without unique words:"
						+ (totalStemmedTokens / totalDocuments));
		
		System.out
		.println("\nAverage word stems per document with unique words:"
				+ (totalUniqueStems/ totalDocuments));


	}

	/**
	 * Method for displaying the token output
	 */
	private static void displayTokenData() {
		int topFrequentTokensCounter = 30;
		System.out.println("\n---------------------------------");
		System.out.println("\n\t\tResult for Token");
		System.out.println("\n---------------------------------");
		System.out.println("\nTotal documents in Cranfield database:"
				+ totalDocuments);
		System.out.println("\nTotal tokens in Cranfield database:"
				+ totalTokens);
		System.out.println("\nTotal unique tokens in Cranfield database:"
				+ totalUniqueTokens);
		System.out
				.println("\nTotal single occurrence tokens in Cranfield database:"
						+ singleCountTokens);

		System.out.println("\n30 most frequent word in Cranfield Database:\n");
		System.out.println("Token\tFrequency");

		for (Map.Entry<String, Integer> topWord : frequentTokenMap.entrySet()) {
			System.out.println(topWord.getKey() + ":  " + topWord.getValue());
			if (topFrequentTokensCounter == 1)
				break;
			topFrequentTokensCounter--;
		}

		System.out
				.println("\nAverage tokens in each document without unique words:"
						+ (totalTokens / totalDocuments));
		System.out
				.println("\nAverage tokens in each document with unique words:"
						+ (totalUniqueTokens / totalDocuments));

	}

	/**
	 * Method to sort the given HashMap based on value.
	 * 
	 * @param tokenMap
	 *            :hashmap of tokens
	 * @return :Returns a sorted TreeMap of tokens
	 */
	private static TreeMap<String, Integer> sortTokensOnFrequency(
			Map<String, Integer> tokenMap) {

		valueComparator comparator = new valueComparator(tokenMap);
		TreeMap<String, Integer> sortedTokenMap = new TreeMap<>(comparator);
		sortedTokenMap.putAll(tokenMap);
		return sortedTokenMap;
	}

	/**
	 * A value comparator sorts the TreeMap values based on token frequency
	 */
	private static class valueComparator implements Comparator<String> {
		Map<String, Integer> mMap;

		public valueComparator(Map<String, Integer> cMap) {
			this.mMap = cMap;
		}

		@Override
		public int compare(String mapVal1, String mapVal2) {
			if (mMap.get(mapVal1) >= mMap.get(mapVal2)) {
				return -1;
			} else {
				return 1;
			}
		}

	}

	/**
	 * Method for reading the input file from the specified file path
	 * 
	 * @param dataDirectory
	 *            :Input file path
	 */
	private static void readDirectory(String dataDirectory) {
		File folder = new File(dataDirectory);
		if (folder.exists() && folder.isDirectory()) {
			File[] cranfieldDatabaseFiles = folder.listFiles();
			for (File file : cranfieldDatabaseFiles) {
				totalDocuments += 1;
				if (file.isFile()) {
					parseSingleFile(file);
				}
			}
		} else {
			System.out.println("Folder Path cannot be found");
		}
	}

	/**
	 * Method for parsing each file from the Cranfield Database
	 * 
	 * @param file
	 *            :File object
	 */
	private static void parseSingleFile(File file) {
		String token = "";
		try {
			Scanner wordScanner = new Scanner(file);
			while (wordScanner.hasNext()) {
				token = wordScanner.next();
				token = token.replaceAll("\\<.*?>", "").replaceAll("\\.", "")
						.trim().toLowerCase();
				token = token.replaceAll("[^a-zA-Z-]+", "");
				if (!token.isEmpty()) {
					if (token.contains("-")) {
						parseHyphenatedTokens(token);
					} else if (token.contains("\'")) {
						parsePosessiveTokens(token);
					}

					else if (token.contains("_")) {
						parseUnderscoreTokens(token);
					} else {
						parseSimpleToken(token);
					}
				}
			}
			wordScanner.close();
		} catch (FileNotFoundException fnf) {
			fnf.printStackTrace();
		}
	}

	/**
	 * Method for Parsing token and calculating token frequency
	 * 
	 * @param token
	 *            :Single token from each file
	 */
	private static void parseSimpleToken(String token) {
		if (!token.isEmpty()) {
			if (tokenMap.containsKey(token)) {
				tokenMap.put(token, tokenMap.get(token) + 1);
			} else {
				tokenMap.put(token, 1);
			}
		}
		stemWords(token);
	}

	/**
	 * Method to calculate stems for each tokens
	 * 
	 * @param token
	 *            :Single token from each file
	 */
	private static void stemWords(String token) {
		String stemmedWords = "";
		Stemmer stem = new Stemmer();
		stem.add(token.toCharArray(), token.length());
		stem.stem();
		stemmedWords = stem.toString();
		if (!stemmedWords.isEmpty()) {
			if (stemmedTokenMap.containsKey(stemmedWords)) {
				stemmedTokenMap.put(stemmedWords,
						stemmedTokenMap.get(stemmedWords) + 1);
			} else {
				stemmedTokenMap.put(stemmedWords, 1);
			}
		}
	}

	/**
	 * Method for parsing tokens having an underscore
	 * 
	 * @param token
	 *            :Single token from each file
	 */
	private static void parseUnderscoreTokens(String token) {
		String[] tokenSplitter = null;
		tokenSplitter = token.split("_");
		for (String splittedToken : tokenSplitter) {
			parseSimpleToken(splittedToken);
		}
	}

	/**
	 * Method for parsing tokens containing apostrophe's
	 * 
	 * @param token
	 *            :Single token from each file
	 */
	private static void parsePosessiveTokens(String token) {
		if (token.startsWith("\'")) {
			token = token.substring(1, token.length());
			parseSimpleToken(token);
		} else if (token.endsWith("\'")) {
			token = token.substring(0, token.length() - 1);
			parseSimpleToken(token);
		} else if (token.endsWith("\'s")) {
			int n = token.indexOf("\'");
			token = token.substring(0, n - 1);
			parseSimpleToken(token);
		} else if (token.endsWith("\'es")) {
			token = token.substring(0, token.length() - 3);
			parseSimpleToken(token);
		} else {
			token = token.replaceAll("'", "");
			parseSimpleToken(token);
		}
	}

	/**
	 * Method for parsing tokens with hyphen
	 * 
	 * @param hyphenToken
	 *            :Single token from each file
	 */
	private static void parseHyphenatedTokens(String hyphenToken) {
		String[] tokenSplitter = null;
		tokenSplitter = hyphenToken.split("-");
		for (String splittedToken : tokenSplitter) {
			parseSimpleToken(splittedToken);
		}
	}

	/**
	 * Method for calculating the total time required for the program
	 */
	public static void timer() {
		if (phase == 0) {
			startTime = System.currentTimeMillis();
			phase = 1;
		} else {
			endTime = System.currentTimeMillis();
			elapsedTime = endTime - startTime;
			System.out.println("Time: " + elapsedTime + " msec.");
			phase = 0;
		}
	}

}
