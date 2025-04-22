package org.example.logmatcher;
import java.util.*;
import java.util.stream.Collectors;

public class StreamMatcher {

    private final Map<String, Set<Integer>> invertedIndex = new HashMap<>(); // word → query IDs
    private final Map<Integer, Integer> queryWordCount = new HashMap<>();    // query ID → word count
    private final List<String> output = new ArrayList<>();
    private int nextQueryId = 1;

    public void processStream(List<String> stream) {
        for (String line : stream) {
            if (line.startsWith("Q: ")) {
                processQuery(line.substring(3).trim());
            } else if (line.startsWith("L: ")) {
                processLog(line.substring(3).trim());
            }
        }
    }

    // Process a query line: tokenize, index words, store query metadata
    private void processQuery(String query) {
        int queryId = nextQueryId++;
        Set<String> words = tokenize(query);

        // Build inverted index
        for (String word : words) {
            invertedIndex.computeIfAbsent(word, k -> new HashSet<>()).add(queryId);
        }

        // Track word count per query for full match check
        queryWordCount.put(queryId, words.size());

        output.add("ACK: " + query + "; ID=" + queryId);
        System.out.println("output "+ output);
        System.out.println(query);
        System.out.println(invertedIndex);
        System.out.println("queryWordCount " +queryWordCount);
    }

    // Process a log line: match against queries and generate output
    private void processLog(String log) {
        Set<String> words = tokenize(log);
        Map<Integer, Integer> matchCounter = countMatches(words);

        List<Integer> fullyMatched = findFullyMatchedQueries(matchCounter);
        if (!fullyMatched.isEmpty()) {
            String matchedOutput = fullyMatched.stream()
                    .map(String::valueOf)
                    .sorted()
                    .collect(Collectors.joining(","));
            output.add("M: " + log + "; Q=" + matchedOutput);
        }
    }

    // Tokenize a string into lowercase words
    private Set<String> tokenize(String text) {
        return new HashSet<>(Arrays.asList(text.toLowerCase().split("\\s+")));
    }

    // Count how many words from each query are matched in this log
    private Map<Integer, Integer> countMatches(Set<String> wordsInLog) {
        Map<Integer, Integer> counter = new HashMap<>();
        for (String word : wordsInLog) {
            for (int queryId : invertedIndex.getOrDefault(word, Set.of())) {
                counter.put(queryId, counter.getOrDefault(queryId, 0) + 1);
            }
        }
        return counter;
    }

    // Return query IDs that matched all their words
    private List<Integer> findFullyMatchedQueries(Map<Integer, Integer> counter) {
        List<Integer> matched = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : counter.entrySet()) {
            int queryId = entry.getKey();
            int matchedWords = entry.getValue();
            if (matchedWords == queryWordCount.get(queryId)) {
                matched.add(queryId);
            }
        }
        return matched;
    }

    public List<String> getOutput() {
        return output;
    }

    // Demo runner
    public static void main(String[] args) {
        List<String> stream = List.of(
                "Q: database",
                "Q: Stacktrace",
                "Q: loading failed",
                "L: Database service started",
                "Q: snapshot loading",
                "Q: fail",
                "L: Started processing events",
                "L: Loading main DB snapshot",
                "L: Loading snapshot failed no stacktrace available"
        );

        StreamMatcher matcher = new StreamMatcher();
        matcher.processStream(stream);
        matcher.getOutput().forEach(System.out::println);
    }
}

