package com.work.collatz.dto;

import java.util.List;

/**
 *
 * @author ajuar
 */
public record CycleResult(long startNumber,
        List<Long> cycle,
        boolean foundCycle,
        long iterations,
        long maxValue) {

    public static CycleResult noCycle(long start, long iterations, long maxValue) {
        return new CycleResult(start, List.of(), false, iterations, maxValue);
    }

    public static CycleResult withCycle(long start, List<Long> cycle, long iterations) {
        long maxValue = cycle.stream().mapToLong(Long::longValue).max().orElse(start);
        return new CycleResult(start, cycle, true, iterations, maxValue);
    }
}
