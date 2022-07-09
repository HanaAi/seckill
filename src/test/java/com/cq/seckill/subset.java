package com.cq.seckill;

import java.util.ArrayList;
import java.util.Arrays;

public class subset {
    public static void main(String[] args){
        int[] arr = {123,456,789};
        System.out.println(getSubsets(arr, arr.length));
    }

    private static ArrayList<ArrayList<Integer>> getSubsets(int[] arr, int n) {
        Arrays.sort(arr);//正序排序，保证按照字典顺序排序
        ArrayList<ArrayList<Integer>> set = new ArrayList();
        for(int i=f(n)-1; i>=1; i--){
            ArrayList a = new ArrayList();
            for(int j=n-1; j>=0; j--){
                if(((i >> j)&1)==1){//判断j位上是否为1,从高位开始检查
                    a.add(arr[j]);
                }
            }
            set.add(a);
        }
        return set;
    }

    private static int f(int n) {
        int sum = 1;
        for(int i=0; i<n; i++){
            sum = sum*2;
        }
        return sum;
    }
}
