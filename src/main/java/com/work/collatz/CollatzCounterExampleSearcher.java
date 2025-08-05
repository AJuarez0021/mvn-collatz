package com.work.collatz;

import com.work.collatz.dto.CycleResult;
import com.work.collatz.dto.SearchResult;
import java.io.Closeable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author ajuar
 */
public class CollatzCounterExampleSearcher implements Closeable{
    private final ExecutorService executor;
    private final long maxSearchValue;
    private final int maxTreeDepth;
    
    public CollatzCounterExampleSearcher(long maxSearchValue, int maxTreeDepth) {
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.maxSearchValue = maxSearchValue;
        this.maxTreeDepth = maxTreeDepth;
    }
    
    // Búsqueda de contraejemplos
    public SearchResult searchForCounterexamples() {
        Instant startTime = Instant.now();
        System.out.println("Starting a comprehensive search for counterexamples...");
        
      
        System.out.println("Generating special candidates...");
        List<Long> specialCandidates = CandidateGenerator.generateAllCandidates(maxSearchValue);
        System.out.printf("Generated %,d special candidates%n", specialCandidates.size());
      
        
        System.out.println("Building inverse Collatz tree...");
        InverseCollatzTree tree = new InverseCollatzTree(maxTreeDepth, maxSearchValue);
        Set<Long> treeNumbers = tree.buildTreeParallel();
        System.out.printf("Tree contains %,d numbers%n", treeNumbers.size());
       
      
        System.out.println("Searching for missing numbers in the tree...");
        List<Long> missingNumbers = tree.findMissingNumbers(1, Math.min(maxSearchValue, 100_000));
        System.out.printf("Found %,d of missing numbers%n", missingNumbers.size());
        
        
        System.out.println("Checking cycles in priority candidates...");
        Set<Long> priorityCandidates = new HashSet<>();
        priorityCandidates.addAll(specialCandidates);
        priorityCandidates.addAll(missingNumbers.subList(0, Math.min(missingNumbers.size(), 1000)));
        
        List<CycleResult> cycleResults = new ArrayList<>();
        List<Future<CycleResult>> futures = new ArrayList<>();
        
        for (Long candidate : priorityCandidates) {
            futures.add(executor.submit(() -> CollatzCycleDetector.detectCycle(candidate)));
        }
        
        for (Future<CycleResult> future : futures) {
            try {
                CycleResult result = future.get();
                if (result.foundCycle() || result.maxValue() > result.startNumber() * 50) {
                    cycleResults.add(result);
                }
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error verifying cycle: " + e.getMessage());
            }
        }
        
       
        Map<String, Object> statistics = compileStatistics(
            specialCandidates, treeNumbers, missingNumbers, cycleResults
        );
        
        Duration executionTime = Duration.between(startTime, Instant.now());
        
        return new SearchResult(
            cycleResults.stream().filter(CycleResult::foundCycle).toList(),
            missingNumbers,
            specialCandidates,
            statistics,
            executionTime
        );
    }
    
    private Map<String, Object> compileStatistics(
            List<Long> specialCandidates,
            Set<Long> treeNumbers,
            List<Long> missingNumbers,
            List<CycleResult> cycleResults) {
        
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("candidatos_especiales", specialCandidates.size());
        stats.put("numeros_en_arbol", treeNumbers.size());
        stats.put("numeros_faltantes", missingNumbers.size());
        stats.put("ciclos_encontrados", cycleResults.stream().filter(CycleResult::foundCycle).count());
        
        // Estadísticas de valores máximos
        OptionalLong maxValueSeen = cycleResults.stream()
            .mapToLong(CycleResult::maxValue)
            .max();
        stats.put("valor_maximo_visto", maxValueSeen.orElse(0));
        
        // Cobertura del árbol
        double coverage = treeNumbers.size() / (double) maxSearchValue * 100;
        stats.put("cobertura_arbol_porcentaje", coverage);
        
        return stats;
    }
    
    @Override
    public void close() {
        executor.shutdown();
    }
}
