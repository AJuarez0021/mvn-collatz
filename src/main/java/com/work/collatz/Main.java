package com.work.collatz;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author linux
 */
public class Main {

    public static void main(String[] args) {
        try {
            testCollatz();
            testIsCollatz(1000_000, 1000_100);
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    private static void testCollatz() {
        System.out.println("==Show Collatz==");
        Random random = new Random();
        int value = random.nextInt(1, 1000);
        int n = CollatzConjecture.showCollatzSequence(value);
        System.out.println("Number of movements: " + n);
    }

    private static void testIsCollatz(long init, long end) throws InterruptedException {
        System.out.println("==IsCollatz==");
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        for (long n = init; n <= end; n++) {
            BigInteger num = BigInteger.valueOf(n);
            executor.submit(() -> {                
                if (!CollatzConjecture.isCollatzSequence(num)) {                    
                    System.err.println("Number " + num + " does not meet Collatz's conjecture");
                } else {                    
                    System.out.println("Number " + num + " meets Collatz's conjecture");
                }
            });
        }

        executor.shutdown();
        int count = 0;
        while (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            System.out.println("Waiting " + count + " ....");
            count++;
        }
    }
}
