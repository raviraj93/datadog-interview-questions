package org.example.logmatcher;

import java.util.*;

/**
 * Optimized StreamingLogMatcher using inverted indexes for fast, sublinear matching.
 * Maps words to log IDs and query IDs (integers) to reduce memory footprint.
 */
public class StreamingLogMatcher {
    /**
     * Represents a search query with a type and keywords.
     */
    public record Query(String type, Set<String> keywords) {
        /**
         * Parses raw input "TYPE: kw1 kw2 ..." or "TYPE kw1 kw2 ..." into a Query.
         */
        public static Query parse(String raw) {
            int idx = raw.indexOf(':');
            String type = (idx >= 0)
                    ? raw.substring(0, idx).trim()
                    : raw.split("\\s+", 2)[0];
            String remainder = (idx >= 0)
                    ? raw.substring(idx + 1).trim()
                    : raw.contains(" ")
                    ? raw.split("\\s+", 2)[1].trim()
                    : "";

            Set<String> kws = new HashSet<>();
            if (!remainder.isEmpty()) {
                for (String w : remainder.toLowerCase().split("\\W+")) {
                    if (!w.isEmpty()) kws.add(w);
                }
            }
            return new Query(type, Collections.unmodifiableSet(kws));
        }
    }

    /**
     * Callback interface for delivering matches.
     */
    public interface LogCallback {
        void onMatch(Query query, String logLine);
    }

    // Storage for logs and queries
    private final List<String> logs = new ArrayList<>();
    private final List<Query> queries = new ArrayList<>();

    // Inverted index: word -> set of log indices
    private final Map<String, Set<Integer>> wordToLogIds = new HashMap<>();
    // Inverted index: word -> set of query indices
    private final Map<String, Set<Integer>> wordToQueryIds = new HashMap<>();

    private final LogCallback callback;

    /**
     * Constructs the matcher with the given callback.
     */
    public StreamingLogMatcher(LogCallback callback) {
        this.callback = callback;
    }

    /**
     * Accepts a new log line: index and match against relevant queries.
     */
    public void acceptLog(String logLine) {
        int logId = logs.size();
        logs.add(logLine);

        // Extract words and index log
        Set<String> words = extractWords(logLine);
        indexWords(words, logId, wordToLogIds);

        // Find candidate queries by union of query sets for each word
        Set<Integer> candidateIds = new HashSet<>();
        for (String w : words) {
            Set<Integer> qIds = wordToQueryIds.get(w);
            if (qIds != null) candidateIds.addAll(qIds);
        }

        // Verify each candidate query ID
        for (Integer qId : candidateIds) {
            Query q = queries.get(qId);
            if (words.containsAll(q.keywords())) {
                callback.onMatch(q, logLine);
            }
        }
    }

    /**
     * Accepts a new query: index and match against past logs.
     */
    public void acceptQuery(Query query) {
        int queryId = queries.size();
        queries.add(query);

        Set<String> kws = query.keywords();
        indexWords(kws, queryId, wordToQueryIds);

        // If no keywords, match all logs
        if (kws.isEmpty()) {
            for (String log : logs) {
                callback.onMatch(query, log);
            }
            return;
        }

        // Intersect log ID sets for keywords
        Iterator<String> it = kws.iterator();
        Set<Integer> hits = new HashSet<>(
                wordToLogIds.getOrDefault(it.next(), Collections.emptySet()));
        while (it.hasNext() && !hits.isEmpty()) {
            hits.retainAll(wordToLogIds.getOrDefault(it.next(), Collections.emptySet()));
        }
        for (Integer logId : hits) {
            callback.onMatch(query, logs.get(logId));
        }
    }

    // Helper to extract unique lowercase words from a line
    private static Set<String> extractWords(String line) {
        Set<String> words = new HashSet<>();
        for (String w : line.toLowerCase().split("\\W+")) {
            if (!w.isEmpty()) words.add(w);
        }
        return words;
    }

    // Generic indexer: map each word to the given item index
    private static void indexWords(Set<String> words, int id, Map<String, Set<Integer>> index) {
        for (String w : words) {
            index.computeIfAbsent(w, k -> new HashSet<>()).add(id);
        }
    }

    /**
     * Demo usage.
     */
    public static void main(String[] args) {
        StreamingLogMatcher matcher = new StreamingLogMatcher((q, l) ->
                System.out.printf("[%s] matched: %s%n", q.type(), l)
        );

        matcher.acceptLog("2025-04-19 INFO Service started");
        matcher.acceptLog("2025-04-19 ERROR Disk failure on /dev/sda");
        matcher.acceptLog("2025-04-19 WARN High memory usage");

        matcher.acceptQuery(Query.parse("ERROR: disk"));
        matcher.acceptQuery(Query.parse("INFO Service"));
        matcher.acceptQuery(Query.parse("WARN memory"));
    }
}
/*
Use BitSet for Indexing

Replace Set<Integer> with BitSet for log/query ID sets to save memory and speed up intersections.

Example: BitSet hits = new BitSet(); hits.and(otherBitSet);

Skip Stopwords and Short Words

Ignore common stopwords (e.g., "the", "a") and words below a certain length during indexing to reduce noise and improve relevance.

Example: if (w.length() <= 2) continue;

Concurrent Data Structures for Thread Safety

Use ConcurrentHashMap for word-to-ID maps and CopyOnWriteArrayList for logs/queries if you need to support concurrent additions.

LRU Cache for Log Storage

Implement a Least Recently Used (LRU) cache for logs to limit memory usage, especially if you only need to match recent logs.

Support Query Types and Boolean Logic

Extend the Query class to support AND/OR logic between keywords for more flexible matching.

Example: public record Query(String type, Set<String> keywords, boolean isAnd) {}

Optimize Word Extraction

Preprocess and normalize words (e.g., lowercase, stemming) to improve matching accuracy and reduce index size.

Batch Processing for Efficiency

If logs and queries arrive in batches, process them together to minimize redundant computations.

Profile and Monitor Performance

Use profiling tools to identify bottlenecks and monitor memory usage in production deployments.
 */
