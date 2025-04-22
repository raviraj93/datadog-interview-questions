package org.example.leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MostCommonWord {
    public String mostCommonWord(String paragraph, String[] banned) {
        Set<String> set = new HashSet<>(Arrays.asList(banned));
        Map<String, Integer> map = new HashMap<>();
        String[] words = paragraph.replaceAll("\\W+" , " ").toLowerCase().split("\\s+");

        for (String word : words) {
            String key = word.toLowerCase();
            if (!set.contains(key)) {
                map.put(key, map.getOrDefault(key, 0) + 1);
            }
        }
        return map.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public static void main(String args[]){
        String paragraph = "Bob hit a ball, the hit BALL flew far after it was hit.\"";
        String[] banned = {"hit"};
        System.out.println(new MostCommonWord().mostCommonWord(paragraph, banned));
    }
}
