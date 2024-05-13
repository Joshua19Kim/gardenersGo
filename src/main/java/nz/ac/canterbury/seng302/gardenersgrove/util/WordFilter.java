package nz.ac.canterbury.seng302.gardenersgrove.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 *  A class used to check whether an input string contains offensive language, based on a user defined list.
 *  Contains static methods that load the offensive words from a .csv file and check whether a string input contains any
 *  words that match those in the list. Support is provided for character replacement and a list of words allowed
 *  in combination with the banned word, e.g. "ass" and "classic".
 */
public class WordFilter {

    static Logger logger = LoggerFactory.getLogger(WordFilter.class);
    static Map<String, String[]> words = new HashMap<>();
    static int largestWordLength = 0;

    /**
     * Loads the list of words from the .csv file into a HashMap. If a banned word has words that are to be ignored in
     * combination with it, these are loaded into the HashMap also.
     */
    public static void loadConfigs() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/static/wordlists/words.csv"));
            String line = "";
            int counter = 0;

            while((line = reader.readLine()) != null) {
                counter++;
                String[] content = null;

                try {
                    content = line.split(",");
                    if(content.length == 0) {
                        continue;
                    }

                    String word = content[0];
                    String[] wordsToIgnore = new String[]{};
                    if(content.length > 1) {
                        wordsToIgnore = content[1].split("_");
                    }

                    if(word.length() > largestWordLength) {
                        largestWordLength = word.length();
                    }

                    words.put(word.replaceAll(" ", ""), wordsToIgnore);
                } catch(Exception e) {
                    logger.error(e.getMessage());
                }
            }
            logger.info("Loaded {} words to be filtered", counter);
        } catch (IOException e) {
            logger.error("IO error while loading config file");
        }
    }

    /**
     * Replaces leetspeak characters in the input string with their alphabetical equivalents.
     * @param input The input string possibly containing leetspeak characters.
     * @return A string with leetspeak characters replaced by their alphabetical equivalents.
     */
    public static String replaceLeetspeak(String input) {
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
     * Iterates over a String input and checks whether a banned word was found, then checks if the word should be
     * ignored.
     * @param input The string input that is to be checked for offensive words.
     * @return A list of offensive words found in the input string.
     */
    public static ArrayList<String> containsBadWords(String input) {
        if(input == null) {
            return new ArrayList<>();
        }

        input = replaceLeetspeak(input);
        ArrayList<String> badWords = new ArrayList<>();
        input = input.toLowerCase().replaceAll("[^a-zA-Z]", "");

        // Iterate over each letter in the word.
        for(int start = 0; start < input.length(); start++) {
            // From each letter, keep going to find bad words until either the end of the sentence is reached, or the max word length is reached.
            for(int offset = 1; offset < (input.length()+1 - start) && offset < largestWordLength; offset++)  {
                String wordToCheck = input.substring(start, start + offset);
                if(words.containsKey(wordToCheck)) {
                    // Check whether the word should be ignored.
                    String[] ignoreCheck = words.get(wordToCheck);
                    boolean ignore = false;
                    for (String string : ignoreCheck) {
                        if (input.contains(string)) {
                            ignore = true;
                            break;
                        }
                    }
                    if(!ignore) {
                        badWords.add(wordToCheck);
                    }
                }
            }
        }

        for(String s: badWords) {
            logger.info("{} detected as a banned word", s);
        }
        return badWords;
    }

    /**
     * Detects whether bad words were found in the input string
     * @param input The string input that is to be checked for offensive words.
     * @return A boolean value that is true if bad words were found and false if not.
     */
    public static Boolean badWordsFound(String input) {
        ArrayList<String> badWords = containsBadWords(input);
        return !badWords.isEmpty();
    }
}
