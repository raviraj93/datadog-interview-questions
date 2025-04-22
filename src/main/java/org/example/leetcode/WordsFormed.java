package org.example.leetcode;

import java.util.HashMap;
import java.util.Map;

public class WordsFormed {
    public int countCharacters(String[] words, String chars) {
        Map<Character, Integer> map = new HashMap<>();
        for (char c : chars.toCharArray()) {
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        int res = 0;
        for (String word : words) {
            if(canFormWord(word, map)){
                res += word.length();
            }
        }
        return res;
    }
    private boolean canFormWord(String word, Map<Character, Integer> map) {
        Map<Character, Integer> temp = new HashMap<>();
        for (char c : word.toCharArray()) {
            temp.put(c, temp.getOrDefault(c, 0) + 1);
        }
        for (Map.Entry<Character, Integer> entry : temp.entrySet()) {
            char ch = entry.getKey();
            int count = entry.getValue();
            if (!map.containsKey(ch) || map.get(ch) < count) {
                return false;
            }
        }
        return true;
    }
}
