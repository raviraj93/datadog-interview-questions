package org.example.leetcode;

public class GCD {

    public String gcdOfStrings(String str1, String str2) {
        return (str1 + str2).equals(str2 + str1) ?
                str1.substring(0, gcd(str1.length(), str2.length())) : "";
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
