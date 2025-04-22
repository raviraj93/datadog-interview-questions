package org.example.bucket;

public class LatencyBuckets {

    public static int[] calcBuckets(int[] latencies, int numberOfBuckets, int bucketWidth) {
        int[] result = new int[numberOfBuckets];

        for (int latency : latencies) {
            int index = latency / bucketWidth;
            if (index >= numberOfBuckets) {
                index = numberOfBuckets - 1;
            }
            result[index]++;
        }

        return result;
    }

    public static void main(String[] args) {
        int[] latencies = {90, 11, 3, 35, 17, 28, 64, 53, 52, 87, 63, 46, 40, 50, 31, 92, 45, 32, 22, 54, 87, 108, 62, 33,
                87, 12, 67, 56, 94, 119, 96, 23, 21, 25, 86, 5, 32, 77, 3, 16, 8, 61, 105, 88, 49, 57, 114, 118, 20, 79, 44,
                55, 113, 23, 13, 86, 16, 81, 1, 111, 84, 76, 24, 54, 110, 7, 100, 40, 3, 37, 96, 37, 67, 48, 79, 47, 108, 36,
                15, 112, 37, 13, 40, 66, 39, 110, 47, 87, 34, 50, 55, 112, 70, 88, 2, 86, 110, 20, 2, 57};

        int[] result = calcBuckets(latencies, 11, 10);
        for (int i = 0; i < result.length; i++) {
            if (i < result.length - 1)
                System.out.printf("%d-%d: %d\n", i * 10, i * 10 + 9, result[i]);
            else
                System.out.printf(">=%d: %d\n", i * 10, result[i]);
        }
    }
}
