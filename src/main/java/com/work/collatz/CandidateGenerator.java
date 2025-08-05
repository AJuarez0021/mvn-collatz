package com.work.collatz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author ajuar
 */
public class CandidateGenerator {
    
    // Números de la forma (2^k * p - 1) / 3 donde p es impar
    public static List<Long> generateSpecialForm(int maxK, List<Integer> oddPrimes) {
        List<Long> candidates = new ArrayList<>();

        for (int k = 1; k <= maxK; k++) {
            long powerOf2 = 1L << k;  // 2^k

            for (int p : oddPrimes) {
                long numerator = powerOf2 * p - 1;
                if (numerator % 3 == 0) {
                    long candidate = numerator / 3;
                    if (candidate > 0) {
                        candidates.add(candidate);
                    }
                }
            }
        }

        return candidates;
    }

    // Números con patrones binarios específicos
    public static List<Long> generateBinaryPatterns(int bitLength) {
        List<Long> candidates = new ArrayList<>();

        // Patrón alternado: 101010...
        StringBuilder alternatingPattern = new StringBuilder();
        for (int i = 0; i < bitLength; i++) {
            alternatingPattern.append(i % 2 == 0 ? "1" : "0");
        }

        try {
            long candidate = Long.parseLong(alternatingPattern.toString(), 2);
            candidates.add(candidate);
        } catch (NumberFormatException e) {
            // Patrón muy largo, ignorar
        }

        // Patrón con muchos 1s consecutivos: 111...000
        for (int onesCount = 3; onesCount < bitLength - 1; onesCount++) {
            StringBuilder pattern = new StringBuilder();
            for (int i = 0; i < onesCount; i++) {
                pattern.append("1");
            }
            for (int i = onesCount; i < bitLength; i++) {
                pattern.append("0");
            }

            try {
                long candidate = Long.parseLong(pattern.toString(), 2);
                candidates.add(candidate);
            } catch (NumberFormatException e) {
                break;
            }
        }

        return candidates;
    }

    // Números congruentes a valores específicos módulo potencias de 2
    public static List<Long> generateModularCandidates(long maxValue) {
        List<Long> candidates = new ArrayList<>();

        // Números de la forma 8k + 3 (más propensos a crecer)
        for (long k = 1; 8 * k + 3 <= maxValue; k++) {
            candidates.add(8 * k + 3);
        }

        // Números de la forma 16k + 15
        for (long k = 1; 16 * k + 15 <= maxValue; k++) {
            candidates.add(16 * k + 15);
        }

        return candidates;
    }

    // Combina todas las estrategias de generación
    public static List<Long> generateAllCandidates(long maxValue) {
        Set<Long> allCandidates = new HashSet<>();

        // Agregar números de forma especial
        List<Integer> smallPrimes = Arrays.asList(3, 5, 7, 11, 13, 17, 19, 23);
        allCandidates.addAll(generateSpecialForm(20, smallPrimes));

        // Agregar patrones binarios
        for (int bitLength = 8; bitLength <= 32; bitLength += 4) {
            allCandidates.addAll(generateBinaryPatterns(bitLength));
        }

        // Agregar candidatos modulares
        allCandidates.addAll(generateModularCandidates(maxValue));

        // Filtrar valores que excedan el límite
        return allCandidates.stream()
                .filter(n -> n <= maxValue)
                .sorted()
                .toList();
    }
}
