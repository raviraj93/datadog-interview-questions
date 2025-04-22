package org.example.leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OddStringDifference {
    public String oddString(String[] words) {
        Map<List<Integer>, List<String>> map = new HashMap<>();

        for (String word : words) {
            char[] chars = word.toCharArray();
            List<Integer> list = new ArrayList<>();
            for(int index = 0; index < chars.length -1; index++) {
                list.add(chars[index +1] - chars[index]);
            }
            if(map.containsKey(list)) {
                map.get(list).add(word);
            } else {
                List<String> list1 = new ArrayList<>();
                list1.add(word);
                map.put(list, list1);
            }
        }
        for(List<Integer> list : map.keySet()) {
            if(map.get(list).size() == 1) {
                return map.get(list).get(0);
            }
        }

        return "";
    }
}
