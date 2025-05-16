package org.example.leetcode.snowflake;

public class MergeSortedLinkedList {
    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode firstHead = list1;
        ListNode secondHead = list2;
        ListNode result = new ListNode();
        ListNode currentNode = result;
        while (firstHead != null && secondHead != null) {
            if (firstHead.val <= secondHead.val) {
                currentNode.next = new ListNode(firstHead.val);
                firstHead = firstHead.next;
            } else {
                currentNode.next = new ListNode(secondHead.val);
                secondHead = secondHead.next;
            }
            currentNode = currentNode.next;
        }
        if (firstHead != null) {
            currentNode.next = firstHead;
        }
        if (secondHead != null) {
            currentNode.next = secondHead;
        }
        return result.next;
    }
}