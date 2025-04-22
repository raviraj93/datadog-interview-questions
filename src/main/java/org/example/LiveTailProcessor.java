package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class LiveTailProcessor {

    public static List<String> processStream(List<String> stream) {
        List<String> result = new ArrayList<>();
        Map<Integer, String> queries = new LinkedHashMap<>();
        int id = 1;

        for (String line : stream) {
            String lowerCase = line.substring(3).trim().toLowerCase();
            if (line.startsWith("Q: ")) {
                queries.put(id, lowerCase);
                result.add("ACK: " + lowerCase + "; ID=" + id);
                id++;
            } else if (line.startsWith("L: ")) {
                List<Integer> matched = new ArrayList<>();

                for (Map.Entry<Integer, String> entry : queries.entrySet()) {
                    if (lowerCase.contains(entry.getValue())) {
                        matched.add(entry.getKey());
                    }
                }

                if (!matched.isEmpty()) {
                    String ids = matched.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));
                    result.add("M: " + lowerCase + "; Q=" + ids);
                }
            }
        }

        return result;
    }

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

        List<String> output = processStream(stream);
        output.forEach(System.out::println);
    }
}
