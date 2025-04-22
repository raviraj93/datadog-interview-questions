package org.example;

import java.util.*;

// addTag:     O(n * k) k = size Of Tag
//searchTags: O(m * d + l * k)     → worst: O(m * n + n * k)
public class HighPerformanceFilter {

    private final Map<String, Set<Integer>> tagIndex = new HashMap<>();
    private final List<String> stream = new ArrayList<>();

    // Adds a new tag line to the stream and indexes each tag
    public void addTag(String tagLine) {
        int index = stream.size();
        for (String tag : tokenize(tagLine)) {
            tagIndex.computeIfAbsent(tag, k -> new HashSet<>()).add(index);
        }
        stream.add(tagLine);
    }

    // Searches for documents that contain all keywords and returns other tags from those
    public Set<String> searchTags(List<String> rawKeywords) {
        Set<String> keywords = new HashSet<>();
        for (String k : rawKeywords) {
            keywords.add(normalize(k));
        }

        Map<Integer, Integer> matchCount = new HashMap<>();
        for (String keyword : keywords) {
            for (int docId : tagIndex.getOrDefault(keyword, Set.of())) {
                matchCount.put(docId, matchCount.getOrDefault(docId, 0) + 1);
            }
        }

        System.out.println(matchCount);

        Set<String> relatedTags = new HashSet<>();
        for (Map.Entry<Integer, Integer> entry : matchCount.entrySet()) {
            if (entry.getValue() == keywords.size()) {
                for (String tag : tokenize(stream.get(entry.getKey()))) {
                    if (!keywords.contains(tag)) {
                        relatedTags.add(tag);
                    }
                }
            }
        }

        return relatedTags;
    }

    // Utility: Normalize and split tag lines
    private Set<String> tokenize(String line) {
        Set<String> tags = new HashSet<>();
        for (String part : line.split(",\\s*")) {
            tags.add(normalize(part));
        }
        return tags;
    }

    // Utility: Lowercase and trim
    private String normalize(String s) {
        return s.trim().toLowerCase();
    }

    public static void main(String[] args) {
        HighPerformanceFilter h = new HighPerformanceFilter();
        h.addTag("apple, facebook, google");
        h.addTag("banana, facebook");
        h.addTag("facebook, google, tesla");
        h.addTag("intuit, google, facebook");

        System.out.println(h.searchTags(Arrays.asList("facebook", "google")));
    }
}
