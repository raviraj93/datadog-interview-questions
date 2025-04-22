package org.example.logmatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LogMatcher {
    private final List<Pattern> searchPatterns = new ArrayList<>();

    // Register a new search query with case-insensitive word matching
    public void addSearchQuery(String query) {
        String regex = "\\b" + Pattern.quote(query) + "\\b";
        searchPatterns.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
    }

    // Process a stream of log entries
    public void processLogStream(BufferedReader logStream) throws IOException {
        String logEntry;
        while ((logEntry = logStream.readLine()) != null) {
            checkLogEntry(logEntry);
        }
    }

    private void checkLogEntry(String logEntry) {
        for (Pattern pattern : searchPatterns) {
            if (pattern.matcher(logEntry).find()) {
                System.out.println("MATCH: " + logEntry);
                return; // Remove if multiple matches per line needed
            }
        }
    }

    public static void main(String[] args) throws IOException {
        LogMatcher matcher = new LogMatcher();

        // Example queries (could be read from input stream)
        matcher.addSearchQuery("error");
        matcher.addSearchQuery("Timeout");
        matcher.addSearchQuery("connection refused");

        // Simulate log stream (replace with actual stream source)
        try (BufferedReader logs = new BufferedReader(
                new InputStreamReader(System.in))) {
            matcher.processLogStream(logs);
        }
    }
}

