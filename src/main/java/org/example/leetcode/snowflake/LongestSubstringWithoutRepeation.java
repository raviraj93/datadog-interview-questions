package org.example.leetcode.snowflake;

import java.util.HashSet;
import java.util.Set;

public class LongestSubstringWithoutRepeation {
    public int lengthOfLongestSubstring(String s) {

        Set<Character> set = new HashSet<>();
        int max = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!set.contains(c)) {
                set.add(c);
                max = Math.max(max, set.size());
            } else {
                set.remove(s.charAt(i - max));
            }
        }
        return max;
    }

    public static void main(String args[]){
        LongestSubstringWithoutRepeation ls = new LongestSubstringWithoutRepeation();
        System.out.println("lengthOfLongestSubstring: " + ls.lengthOfLongestSubstring("aab"));


    }
}
