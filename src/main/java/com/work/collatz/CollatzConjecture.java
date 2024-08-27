package com.work.collatz;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author linux
 */
public final class CollatzConjecture {

    private static final ConcurrentHashMap<BigInteger, Boolean> memoizationMap = new ConcurrentHashMap<>();

    private CollatzConjecture() {
    }

    public static int showCollatzSequence(long n) {
        int cont = 0;
        if (n <= 0) {
            throw new IllegalArgumentException("Value  " + n + " is not valid");
        }
        System.out.print("Sequence for " + n + " => ");
        while (n != 1) {
            System.out.print(n + " ");
            if (n % 2 == 0) {
                n = n / 2;
            } else {
                n = 3 * n + 1;
            }
            cont++;
        }
        // Imprimir 1 al final de la secuencia
        System.out.println(1);  
        return cont;
    }

    public static boolean isCollatzSequence(BigInteger n) {
        if (n.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalArgumentException("Value " + n + " is not valid");
        }
                
        Set<BigInteger> seen = new HashSet<>();
        while (!n.equals(BigInteger.ONE) && !seen.contains(n)) {
            seen.add(n);

            if (memoizationMap.containsKey(n)) {
                return memoizationMap.get(n);
            }

            if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
                n = n.divide(BigInteger.TWO);
            } else {
                n = n.multiply(BigInteger.valueOf(3)).add(BigInteger.ONE);
            }
        }

        boolean converges = n.equals(BigInteger.ONE);
        for (BigInteger num : seen) {
            memoizationMap.put(num, converges);
        }

        return converges;
    }
}
