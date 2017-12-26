package com.gen;

import java.util.Random;

public class SimpleTest {
    public static void main(String[] args) {
        int taMaxCost=10;//50
        int taMinCost=1;//500
        int randRedPackCost=new Random().nextInt(taMaxCost - taMinCost + 1) + taMaxCost;
        int count=10;
        int money=120;
        //120/10=12,
        //int maxCostBalance=
        System.out.println((double) money/(double)count);

    }
}
