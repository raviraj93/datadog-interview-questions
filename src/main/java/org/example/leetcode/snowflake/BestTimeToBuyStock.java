package org.example.leetcode.snowflake;

import static java.util.Collections.max;

public class BestTimeToBuyStock {
    public int maxProfit(int[] prices) {

        int buyPrice = prices[0];
        int profit = 0;
        for (int price : prices) {
            buyPrice = Math.min(buyPrice, price);
            profit = Math.max(profit, price - buyPrice);
        }
        return profit;
    }
}
