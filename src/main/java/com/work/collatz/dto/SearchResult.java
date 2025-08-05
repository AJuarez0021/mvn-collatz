package com.work.collatz.dto;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ajuar
 */
public record SearchResult(
        List<CycleResult> cyclesFound,
        List<Long> missingFromTree,
        List<Long> specialCandidates,
        Map<String, Object> statistics,
        Duration executionTime) {

    public boolean hasCounterexamples() {
        return !cyclesFound.isEmpty() || !missingFromTree.isEmpty();
    }
}
