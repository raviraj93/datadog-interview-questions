package org.example.leetcode.snowflake;

import java.util.HashSet;
import java.util.Set;

public class IsHappyNumber {
    public boolean isHappy(int n) {

        Set<Integer> set = new HashSet<>();
        while(n != 1 && !set.contains(n)){
            set.add(n);
            n = getSum(n);
        }
        return n == 1;
    }

    private int getSum(int n) {
        int sum = 0;
        while (n != 0) {
            int digit = n % 10;
            sum += digit * digit;
            n /= 10;
        }
        return sum;
    }

    public static void main(String[] args) {
        IsHappyNumber isHappyNumber = new IsHappyNumber();
        System.out.println(isHappyNumber.isHappy(19));
    }
}
