package org.example.leetcode.snowflake;

import java.util.HashMap;
import java.util.Map;

public class RomanToInt {

    public int romanToInt(String s) {

        Map<Character, Integer> map = new HashMap<>();

        map.put('I', 1);
        map.put('V', 5);
        map.put('X', 10);
        map.put('L', 50);
        map.put('C', 100);
        map.put('D', 500);
        map.put('M', 1000);

        int ans =0;

        for (int i = 0; i < s.length(); i++) {
          if(i < s.length() -1 && map.get(s.charAt(i)) < map.get(s.charAt(i+1))) {
              ans -= map.get(s.charAt(i));
          } else {
              ans += map.get(s.charAt(i));
          }
        }

        return  ans;
    }

    public static void main(String args[]){
        RomanToInt romanToInt = new RomanToInt();
        System.out.println(romanToInt.romanToInt("III"));
        System.out.println(romanToInt.romanToInt("LVIII"));
        System.out.println(romanToInt.romanToInt("MCMXCIV"));
        System.out.println(romanToInt.romanToInt("IV"));


    }
}
