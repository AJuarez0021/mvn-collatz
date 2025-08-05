package com.work.collatz;

import com.work.collatz.dto.CycleResult;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author ajuar
 */
public class CollatzCycleDetector {

    private static final long MAX_ITERATIONS = 10_000L;
    private static final long MAX_VALUE_THRESHOLD = 1_000_000_000L;

    // Detecta ciclos usando el algoritmo de Floyd
    public static CycleResult detectCycle(long startNumber) {
        if (startNumber <= 0) {
            throw new IllegalArgumentException("The number must be positive");
        }

        long tortoise = startNumber;
        long hare = startNumber;
        long iterations = 0;
        long maxValue = startNumber;

        // Fase 1: Detectar si hay un ciclo
        do {
            try {
                tortoise = CollatzConjecture.collatzStep(tortoise);
                maxValue = Math.max(maxValue, tortoise);
                iterations++;

                if (tortoise == 1) {
                    return CycleResult.noCycle(startNumber, iterations, maxValue);
                }

                hare = CollatzConjecture.collatzStep(hare);
                maxValue = Math.max(maxValue, hare);

                if (hare != 1) {
                    hare = CollatzConjecture.collatzStep(hare);
                    maxValue = Math.max(maxValue, hare);
                }

                iterations += 2;

                if (iterations > MAX_ITERATIONS || maxValue > MAX_VALUE_THRESHOLD) {
                    return CycleResult.noCycle(startNumber, iterations, maxValue);
                }

            } catch (ArithmeticException e) {
                System.err.println("Overflow detected for number: " + startNumber);
                return CycleResult.noCycle(startNumber, iterations, maxValue);
            }

        } while (tortoise != hare && hare != 1);

        if (hare == 1) {
            return CycleResult.noCycle(startNumber, iterations, maxValue);
        }

        // Fase 2: Encontrar el inicio del ciclo
        long cycleStart = startNumber;
        while (cycleStart != tortoise) {
            cycleStart = CollatzConjecture.collatzStep(cycleStart);
            tortoise = CollatzConjecture.collatzStep(tortoise);
        }

        // Fase 3: Construir el ciclo completo
        List<Long> cycle = new ArrayList<>();
        long current = cycleStart;
        do {
            cycle.add(current);
            current = CollatzConjecture.collatzStep(current);
        } while (current != cycleStart);

        return CycleResult.withCycle(startNumber, cycle, iterations);
    }

    // Busca ciclos en un rango
    public static List<CycleResult> searchCyclesInRange(long start, long end) {
        List<CycleResult> results = new CopyOnWriteArrayList<>();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            for (long i = start; i <= end; i++) {
                final long number = i;
                futures.add(executor.submit(() -> {
                    try {
                        CycleResult result = detectCycle(number);

                        if (result.foundCycle() || result.maxValue() > number * 10) {
                            results.add(result);

                            // Posible contraejemplo encontrado
                            if (result.foundCycle()) {
                                System.out.println("¡CYCLE FOUND!");
                                System.out.println("Initial number: " + number);
                                System.out.println("Cycle: " + result.cycle());
                            }
                        }

                        if (number % 10_000 == 0) {
                            System.out.printf("Verified up to: %,d (Maximum value seen: %,d)%n",
                                    number, result.maxValue());
                        }

                    } catch (Exception e) {
                        System.err.println("Error processing " + number + ": " + e.getMessage());
                    }
                }));
            }

            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    System.err.println("Error in execution: " + e.getMessage());
                }
            }
        }

        return results;
    }
}
