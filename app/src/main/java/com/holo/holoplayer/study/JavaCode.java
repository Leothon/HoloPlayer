package com.holo.holoplayer.study;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

/**
 * @Author: a10943
 * @Date: 2020/9/27
 * @Desc: 测试用的main类
 */
public class JavaCode {
    public static void main(String[] args) {

//        FruitFactory fruitFactory = new FruitFactory();
//        Fruit apple = fruitFactory.createFruit("苹果");
//        Fruit orange = fruitFactory.createFruit("橙子");
//        Fruit banana = fruitFactory.createFruit("香蕉");
//        apple.eat();
//        orange.eat();
//        banana.eat();

//        IFactory factoryApple = new AppleFactory();
//        Fruit apple = factoryApple.create();
//        apple.eat();
//
//        IFactory factoryOrange = new OrangeFactory();
//        Fruit orange = factoryOrange.create();
//        orange.eat();

    }

    public static boolean isStraight(int[] nums) {
        Set<Integer> repeat = new HashSet<>();
        int min = 14,max = 0;

        for (int num : nums) {
            if (num == 0) continue;
            min = Math.min(min,num);
            max = Math.max(max,num);
            if (repeat.contains(num)) {
                return false;
            }
            repeat.add(num);
        }
        return max - min < 5;
    }

    private static String reverseString(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] ch = s.toCharArray();
        int n = ch.length;
        for (int i = 0;i < n / 2;i ++) {
            char temp = ch[i];
            ch[i] = ch[n - 1 - i];
            ch[n - 1 - i] = temp;
        }
        for (char c : ch) {
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    class ListNode {
      int val;
      ListNode next;
      ListNode(int x) {
          val = x;
          next = null;
      }
    }

    public class TreeNode {
      int val;
      TreeNode left;
      TreeNode right;
      TreeNode(int x) { val = x; }
    }


    public TreeNode helper(int[] nums,int left,int right) {
        if (left > right) {
            return null;
        }
        int mid = (right + left) / 2;
        TreeNode root = new TreeNode(nums[mid]);
        root.left = helper(nums,left,mid - 1);
        root.right = helper(nums,mid + 1, right);
        return root;
    }

//    public boolean hasCycle(ListNode head) {
//        Set<ListNode> seen = new HashSet<>();
//        while(head != null) {
//            if (!seen.add(head)) {
//                return true;
//            }
//            head = head.next;
//        }
//        return false;
//    }

    public boolean hasCycle(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }
        // 定义两个指针，一个快，一个慢，如果成环，肯定会相遇
        ListNode slow = head;
        ListNode fast = head.next;
        while(slow != fast) {
            if (fast == null || fast.next == null) {
                return false;
            }
            slow = slow.next;
            fast = fast.next.next;
        }
        return true;
    }

    public boolean isPalindrome(ListNode head) {
        // 将链表复制到数组
        List<Integer> listNodes = new ArrayList<>();
        ListNode current = head;
        while (current != null) {
            listNodes.add(current.val);
            current = current.next;
        }

        int front = 0;
        int end = listNodes.size() - 1;
        while (front < end) {
            if (!listNodes.get(front).equals(listNodes.get(end))) {
                return false;
            }
            front ++;
            end --;
        }
        return true;
    }
}
