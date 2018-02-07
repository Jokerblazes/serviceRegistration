package com.joker.registration.utils;

import java.util.Random;

/**
 * @Author Joker
 * @Description
 * @Date Create in 上午10:05 2018/2/5
 */
public class RandomUtils {
    private RandomUtils() {}
    private static final Random random = new Random();

    /**
     * 生成[0,i]的随机数
     * @param i
     * @return
     */
    public static int getRandomFromRange(int i) {
        return random.nextInt(i);
    }
}
