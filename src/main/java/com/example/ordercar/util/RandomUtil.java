package com.example.ordercar.util;

import java.util.Random;

public class RandomUtil {

    public static String getRandomNumber() {

        Random random = new Random();
        int number = random.nextInt(111111, 999999);
        return String.valueOf(number);
    }

}
