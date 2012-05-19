package com.codebits.accumulo;

import java.util.Random;

public class RandomDriver {

    public static void main(String[] args) {
        System.out.println("START");
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
        	System.out.println(random.nextInt(1000));
        }
    }
}
