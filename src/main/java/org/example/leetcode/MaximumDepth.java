package org.example.leetcode;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MaximumDepth {
    static class Node {
        public int val;
        public List<Node> children;

        public Node() {
        }

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, List<Node> _children) {
            val = _val;
            children = _children;
        }
    }

    // Time Complexity is O(N) as every node is visited once.
    public int maxDepth(Node root) {
        if (root == null) {
            return 0;
        }
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        int depth = 0;
        while (!queue.isEmpty()) {
          int size = queue.size();
          for (int i = 0; i < size; i++) {
              Node node = queue.poll();
              queue.addAll(node.children);
          }
          depth++;
        }
        return depth;
    }

    public int maxDepthRecursive(Node root) {
        if (root == null) {
            return 0;
        }

        int max = 0;
        for (Node child : root.children) { //replace left&right to for loop
            int value = maxDepthRecursive(child);

            if (value > max) {
                max = value;
            }
        }
        return max +1;
    }

    public static void main(String[] args) throws IOException {
        MaximumDepth maxDepth = new MaximumDepth();
        System.out.println("Maximum Depth: " + maxDepth.maxDepth(new Node()));
    }

}
