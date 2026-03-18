package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


class Solution {
    public int searchInsert(int[] nums, int target) {


        int right = nums.length-1;
        int left = 0;

        while(left <= right){
            int mid = left + (right - left) / 2;
            if (nums[mid] == target)
                return mid;
            else if(target > nums[mid]){
                left = mid + 1;
            }
            else{
                right = mid - 1;
            }

        }
        return left;

    }
}

@SpringBootApplication
public class DemoApplication {



    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }



    int[] nums = {1,3,5,6};
    int target = 5;

    Solution solution = new Solution();

    solution.searchInsert

}
