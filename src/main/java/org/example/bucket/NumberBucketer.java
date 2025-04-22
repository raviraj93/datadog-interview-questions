package org.example.bucket;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility to bucket (group) numbers according to a provided classifier function.
 */
public class NumberBucketer {

    /**
     * Buckets a collection of numbers into lists based on the classifier.
     *
     * @param numbers    the input collection of numbers
     * @param classifier function mapping each number to a bucket key
     * @param <K>        the type of bucket key
     * @return a map from bucket key to list of numbers in that bucket
     */
    public static <K> Map<K, List<Integer>> bucketBy(Collection<Integer> numbers,
                                                     Function<Integer, K> classifier) {
        // Using Java Streams for concise grouping
        return numbers.stream()
                .collect(Collectors.groupingBy(classifier, LinkedHashMap::new, Collectors.toList()));
    }

    /**
     * Example classifier: bucket by even or odd.
     */
    public static String parityBucket(int n) {
        return (n % 2 == 0) ? "Even" : "Odd";
    }

    /**
     * Example classifier: bucket by range size.
     * e.g., bucketSize=10: 0-9, 10-19, etc.
     */
    public static String rangeBucket(int n, int bucketSize) {
        int low = (n / bucketSize) * bucketSize;
        int high = low + bucketSize - 1;
        return String.format("%d-%d", low, high);
    }

    /**
     * Demo usage.
     */
    public static void main(String[] args) {
        List<Integer> nums = Arrays.asList(3, 7, 12, 19, 20, 25, 30, 33, 40);

        // Bucket by parity
        Map<String, List<Integer>> byParity = bucketBy(nums, NumberBucketer::parityBucket);
        System.out.println("By parity:");
        byParity.forEach((k, v) -> System.out.println(k + " -> " + v));

        // Bucket by ranges of size 10
        Map<String, List<Integer>> byRange = bucketBy(nums, n -> rangeBucket(n, 10));
        System.out.println("\nBy range (10s):");
        byRange.forEach((k, v) -> System.out.println(k + " -> " + v));
    }
}
