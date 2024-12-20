package nz.ac.canterbury.seng302.gardenersgrove.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * A class used to check whether an input string contains offensive language, based on a user defined list.
 * Contains static methods that load the offensive words from a .csv file and check whether a string input contains any
 * words that match those in the list. Support is provided for character replacement and a list of words allowed
 * in combination with the banned word, e.g. "ass" and "classic".
 */
public class WordFilter {

    static Logger logger = LoggerFactory.getLogger(WordFilter.class);
    static Map<String, String[]> words = new HashMap<>();
    static int largestWordLength = 0;

    static {
        loadConfigs();
    }

    /**
     * Processes each .csv file in the wordlists directory.
     */
    private static void loadConfigs() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream wordlistsStream = classLoader.getResourceAsStream("static/wordlists");
            if (wordlistsStream == null) {
                logger.error("Wordlists directory not found");
                return;
            }
            Path wordlistsPath = Paths.get(Objects.requireNonNull(classLoader.getResource("static/wordlists")).toURI());
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(wordlistsPath, "*.csv")) {
                for (Path entry : stream) {
                    processFile(entry);
                }
            }
        } catch (Exception e) {
            logger.error("Error while loading config files", e);
        }
    }

    /**
     * Loads the list of words from the .csv file into a HashMap. If a banned word has words that are to be ignored in
     * combination with it, these are loaded into the HashMap also.
     * Wordlist was sourced from: <a href="https://github.com/LDNOOBW/List-of-Dirty-Naughty-Obscene-and-Otherwise-Bad-Words">...</a>
     */
    private static void processFile(Path filePath) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(filePath)))) {
            String line;
            int counter = 0;

            while ((line = reader.readLine()) != null) {
                counter++;
                String[] content = line.split(",");
                if (content.length == 0) {
                    continue;
                }

                String word = content[0];
                String[] wordsToIgnore = new String[]{};
                if (content.length > 1) {
                    wordsToIgnore = content[1].split("_");
                }

                if (word.length() > largestWordLength) {
                    largestWordLength = word.length();
                }

                words.put(word.replaceAll(" ", ""), wordsToIgnore);
            }
            logger.info("Loaded {} words from file {}", counter, filePath.getFileName());
        } catch (IOException e) {
            logger.error("IO error while processing file {}", filePath.getFileName(), e);
        }
    }

    /**
     * Helper method that replaces leetspeak characters in the input string with their alphabetical equivalents.
     *
     * @param input The input string possibly containing leetspeak characters.
     * @return A string with leetspeak characters replaced by their alphabetical equivalents.
     */
    private static String replaceLeetspeak(String input) {
        input = input.replaceAll("0", "o")
                .replaceAll("1", "i")
                .replaceAll("3", "e")
                .replaceAll("4", "a")
                .replaceAll("5", "s")
                .replaceAll("7", "t")
                .replaceAll("9", "g");
        return input;
    }

    /**
     * Helper method to find bad words in a string. Returns a set so that duplicated can be filtered later.
     *
     * @param input The input string to be checked for bad words.
     * @return A Set containing the bad words found.
     */
    private static Set<String> findBadWords(String input) {
        Set<String> badWords = new HashSet<>();
        input = input.toLowerCase().replaceAll("[^a-zA-Z]", "");

        // Iterate over each letter in the word.
        for (int start = 0; start < input.length(); start++) {
            // From each letter, keep going to find bad words until either the end of the sentence is reached, or the max word length is reached.
            for (int offset = 1; offset < (input.length() + 1 - start) && offset < largestWordLength; offset++) {
                String wordToCheck = input.substring(start, start + offset);
                if (words.containsKey(wordToCheck)) {
                    // Check whether the word should be ignored.
                    String[] ignoreCheck = words.get(wordToCheck);
                    boolean ignore = false;
                    for (String s : ignoreCheck) {
                        if (input.contains(s)) {
                            ignore = true;
                            break;
                        }
                    }
                    if (!ignore) {
                        badWords.add(wordToCheck);
                    }
                }
            }
        }
        return badWords;
    }

    /**
     * Iterates over a String input and checks whether a banned word was found, then checks if the word should be
     * ignored. The original input and leetspeak-replaced input are both checked.
     *
     * @param input The string input that is to be checked for offensive words.
     * @return A list of offensive words found in the input string.
     */
    private static ArrayList<String> containsBadWords(String input) {
        if (input == null) {
            return new ArrayList<>();
        }

        Set<String> badWords = findBadWords(input); // Check original input
        String cleanedInput = replaceLeetspeak(input);
        badWords.addAll(findBadWords(cleanedInput)); // Check leetspeak-replaced input

        ArrayList<String> badWordsList = new ArrayList<>(badWords);
        for (String s : badWordsList) {
            logger.info("'{}' detected as a banned word", s);
        }
        return badWordsList;
    }

    /**
     * Detects whether bad words were found in the input string
     *
     * @param input The string input that is to be checked for offensive words.
     * @return A boolean value that is true if bad words were found and false if not.
     */
    public static Boolean doesContainBadWords(String input) {
        ArrayList<String> badWords = containsBadWords(input);
        return !badWords.isEmpty();
    }
}
