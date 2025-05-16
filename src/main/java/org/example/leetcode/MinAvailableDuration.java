package org.example.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MinAvailableDuration {
    public List<Integer> minAvailableDuration(int[][] slots1, int[][] slots2, int duration) {
        List<Integer> res = new ArrayList<>();
        //sort both arrays as per starting time of every interval
        Comparator<int[]> comp = Comparator.comparingInt(i->i[0]);
        Arrays.sort(slots1, comp);
        Arrays.sort(slots2, comp);
        //start scanning, keep pointer of every slot array
        int p1 = 0, p2 = 0;
        while (p1 < slots1.length && p2 < slots2.length) {
            int[] slot1 = slots1[p1];
            int[] slot2 = slots2[p2];
            //check if there is intersection
            int start = Math.max(slot1[0], slot2[0]);
            int end = Math.min(slot1[1], slot2[1]);
            //if intersection if long enough - this is our answer
            if (end - start >= duration) {
                res.add(start);
                res.add(start + duration);
                break;
            }
            //if no good intersection - move to the next slot in array that has the slot that
            //ends earlier
            if (slot1[1] < slot2[1])
                p1++;
            else
                p2++;
        }

        return res;
    }
}
