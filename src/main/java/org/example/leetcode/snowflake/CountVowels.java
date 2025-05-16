package org.example.leetcode.snowflake;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CountVowels {
    public int countVowelSubstrings(String word) {

        int count = 0;
        Set<Character> set = new HashSet<>();
        set.add('a');
        set.add('e');
        set.add('i');
        set.add('o');
        set.add('u');
        Set<Character> tempSet = new HashSet<>();
        for (int i = 0; i < word.length() - 4; i++) {
            tempSet.clear();
          for (int j = i; j < word.length() ; j++) {
              char c = word.charAt(j);
              if (set.contains(c)) {
                  tempSet.add(c);
                  if (tempSet.size() == 5) {
                      count++;
                  }
              } else {
                  break;
              }
          }
        }
        return count;
    }

    public static void main(String[] args) {
//        System.out.println(new CountVowels().countVowelSubstrings("aeiouu"));
//        System.out.println(new CountVowels().countVowelSubstrings("unicornarihan"));
        System.out.println(new CountVowels().countVowelSubstrings("cuaieuouac"));
    }
}
