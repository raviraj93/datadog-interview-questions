package org.example.wordfrequency;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility to count word frequencies in a block of text.
 */
public class WordFrequencyCounter {
    // Precompiled regex to match words (sequence of word characters)
    private static final Pattern WORD_PATTERN = Pattern.compile("\\b\\w+\\b"); // Pattern for tokenizer

    /**
     * Counts the occurrences of each word in the given text. Case-insensitive.
     *
     * @param text the input paragraph
     * @return a map from lowercase word to its frequency count
     */
    public static Map<String, Integer> countFrequencies(String text) {
        Map<String, Integer> freq = new HashMap<>();
        Matcher matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            // Extract the matched word and normalize to lowercase
            String word = matcher.group().toLowerCase();
            // Increment the count for this word
            freq.put(word, freq.getOrDefault(word, 0) + 1);
        }
        return freq;
    }

    public static Map<String, Integer> countFrequenciesSplit(String text) {
        Map<String, Integer> freq = new HashMap<>();
        // Split on non-letter/digit/underscore characters
        String[] tokens = text.toLowerCase().split("\\W+");
        for (String token : tokens) {
            if (token.isEmpty()) continue;
            freq.put(token, freq.getOrDefault(token, 0) + 1);
        }
        return freq;
    }

    /**
     * Counts the occurrences of each word in the given text without using regex or Pattern.
     * Scans characters manually, treating letters, digits, and underscore as part of words.
     * Case-insensitive.
     *
     * @param text the input paragraph
     * @return a map from lowercase word to its frequency count
     */
    public static Map<String, Integer> countFrequenciesManual(String text) {
        Map<String, Integer> freq = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // Check if character is letter, digit, or underscore
            if (Character.isLetterOrDigit(c) || c == '_') {
                sb.append(Character.toLowerCase(c));
            } else {
                if (!sb.isEmpty()) {
                    String word = sb.toString();
                    freq.put(word, freq.getOrDefault(word, 0) + 1);
                    sb.setLength(0);
                }
            }
        }
        // Handle last token
        if (!sb.isEmpty()) {
            String word = sb.toString();
            freq.put(word, freq.getOrDefault(word, 0) + 1);
        }
        return freq;
    }
    /**
     * Demo usage.
     */
    public static void main(String[] args) {
        String paragraph = "In a village of La Mancha, the name of which I have no desire to call " +
                "to mind, there lived not long since one of those gentlemen that keep a lance " +
                "in the lance-rack, an old buckler, a lean hack, and a greyhound for coursing.";

        Map<String, Integer> frequencies = countFrequencies(paragraph);
        System.out.println("Word Frequencies:");
        frequencies.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> System.out.format("%s: %d%n", e.getKey(), e.getValue()));
    }
}
