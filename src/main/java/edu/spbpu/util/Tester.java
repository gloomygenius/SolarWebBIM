package edu.spbpu.util;

import lombok.RequiredArgsConstructor;

import java.util.stream.IntStream;

/**
 * Created by Vasiliy Bobkov on 03.02.2017.
 */
@RequiredArgsConstructor
public class Tester {
    private long timer = System.currentTimeMillis();
    private final String title;

    public void resetTimer() {
        timer = System.currentTimeMillis();
    }

    public void countTime() {
        timer = System.currentTimeMillis() - timer;
        System.out.println(title + " time: " + timer);
    }

    public static void main(String[] args) {
        IntStream.range(0, 24).forEach(System.out::println);
    }
}
