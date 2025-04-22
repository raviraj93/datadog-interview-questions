package org.example.leetcode;

import java.util.Collections;
import java.util.PriorityQueue;

public class SlidingWindowMedian {
    private final PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    private final PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());

    public double[] medianSlidingWindow(int[] nums, int k) {

        double[] median = new double[nums.length - k + 1];
        for(int i=0; i < nums.length; i++){
            add(nums[i]);
            // When the index reaches size of k, we can find the median and remove the first element in the window
            if (i + 1 >= k) {
                median[i-k+1] = findMedian();
                remove(nums[i-k+1]);
            }
        }
        return median;
    }

    // For odd number of elements, top most element in maxHeap is the median of the current window,
    // else mean of maxHeap top & minHeap top represents the median
    private double findMedian(){
        return maxHeap.size() > minHeap.size() ? maxHeap.peek() : minHeap.peek() / 2.0 + maxHeap.peek() / 2.0;
    }

    // This method adds the next element in the sliding window in the appropriate heap and rebalances the heaps
    private void add(int num){
        if (maxHeap.isEmpty() || maxHeap.peek() >= num)
            maxHeap.add(num);
        else minHeap.add(num);
        rebalanceHeaps();
    }

    // This method removes the first element in the sliding window from the appropriate heap and rebalances the heaps
    private void remove(int num){
        if (num > maxHeap.peek())
            minHeap.remove(num);
        else maxHeap.remove(num);
        rebalanceHeaps();
    }

    // This method keeps the height of the 2 heaps same
    private void rebalanceHeaps(){
        if (maxHeap.size() == minHeap.size())
            return;
        if (maxHeap.size() > minHeap.size() + 1)
            minHeap.add(maxHeap.poll());
        else if (maxHeap.size() < minHeap.size())
            maxHeap.add(minHeap.poll());
    }
}
