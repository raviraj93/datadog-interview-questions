package org.example;

import java.util.*;

public class TagStreamFilter {

    public static Set<String> filterTags(List<String> stream, List<String> keywords) {
        Set<String> keywordSet = new HashSet<>();
        for (String kw : keywords) {
            keywordSet.add(kw.trim());
        }

        Set<String> result = new HashSet<>();

        for (String line : stream) {
            Set<String> tags = new HashSet<>();
            for (String tag : line.split(",")) {
                tags.add(tag.trim());
            }

            // Check if all keywords are present
            if (tags.containsAll(keywordSet)) {
                for (String tag : tags) {
                    if (!keywordSet.contains(tag)) {
                        result.add(tag);
                    }
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        List<String> stream = List.of(
                "apple, facebook, google",
                "banana, facebook",
                "facebook, google, tesla",
                "intuit, google, facebook"
        );

        System.out.println("Keywords: [apple] → " + filterTags(stream, List.of("apple")));
        System.out.println("Keywords: [facebook, google] → " + filterTags(stream, List.of("facebook", "google")));
    }
}

