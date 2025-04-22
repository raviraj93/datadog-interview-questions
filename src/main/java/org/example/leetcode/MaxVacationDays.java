package org.example.leetcode;

import java.util.Arrays;

//Time complexity : O(n2k)
public class MaxVacationDays {

    public int maxVacationDays(int[][] flights, int[][] days) {
     int[][] dp = new int[flights.length][days.length];
     for(int [] line : dp) {
         Arrays.fill(line, Integer.MIN_VALUE);
     }
     return dfs(flights, days, 0, 0, dp);
    }

    private int dfs(int[][] flights, int[][] days, int currentCity, int weekNumber, int[][] dp) {
        if(weekNumber == days[0].length) {
            return 0;
        }
        if(dp[currentCity][weekNumber] != Integer.MIN_VALUE) {
            return dp[currentCity][weekNumber];
        }
        int maxVacationDays = 0;
        for(int city = 0; city < flights.length; city++) {
            if(flights[currentCity][city] == 1 || currentCity == city) {
                int vacationDays = days[city][weekNumber] + dfs(flights, days, city, weekNumber + 1, dp);
                maxVacationDays = Math.max(maxVacationDays, vacationDays);
            }
        }
        dp[currentCity][weekNumber] = maxVacationDays;
        return maxVacationDays;
    }
}
