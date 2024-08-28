package com.example.fyp.map;

import java.util.concurrent.ThreadLocalRandom;

public class RandomCodeGenerator {

    public static void main(String[] args) {
        // Generate a random 4-digit code
        int code = generateRandomCode();
        System.out.println("Generated code: " + code);
    }

    public static int generateRandomCode() {
        // Generate a random integer between 1000 and 9999 (inclusive)
        return ThreadLocalRandom.current().nextInt(1000, 10000);
    }
}
