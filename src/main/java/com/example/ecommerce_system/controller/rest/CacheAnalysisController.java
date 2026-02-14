package com.example.ecommerce_system.controller.rest;

import com.example.ecommerce_system.config.RequireAdmin;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/cache-analysis")
@RequireAdmin
@AllArgsConstructor
public class CacheAnalysisController {

    private final CacheManager cacheManager;
    private static final Map<String, CacheSnapshot> baselineSnapshots = new HashMap<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CacheSnapshot {
        private String cacheName;
        private long hitCount;
        private long missCount;
        private double hitRate;
        private long requestCount;
        private long loadCount;
        private double averageLoadTime;
        private long evictionCount;
        private LocalDateTime timestamp;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CacheComparison {
        private String cacheName;
        private CacheSnapshot baseline;
        private CacheSnapshot current;
        private long hitCountDelta;
        private long missCountDelta;
        private double hitRateImprovement;
        private long requestsSaved;
        private double performanceGain;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceReport {
        private LocalDateTime reportTime;
        private List<CacheComparison> cacheComparisons;
        private double overallHitRate;
        private long totalHits;
        private long totalMisses;
        private String summary;
        private List<String> topPerformers;
        private List<String> recommendations;
    }

    /**
     * Capture baseline performance (before cache benefits analysis)
     */
    @PostMapping("/baseline")
    public ResponseEntity<String> captureBaseline() {
        baselineSnapshots.clear();
        for (String cacheName : cacheManager.getCacheNames()) {
            CacheSnapshot snapshot = getCurrentSnapshot(cacheName);
            baselineSnapshots.put(cacheName, snapshot);
        }
        return ResponseEntity.ok("Baseline captured for " + baselineSnapshots.size() + " caches at " + LocalDateTime.now());
    }

    /**
     * Get current cache statistics
     */
    @GetMapping("/current-stats")
    public ResponseEntity<List<CacheSnapshot>> getCurrentStats() {
        List<CacheSnapshot> stats = cacheManager.getCacheNames()
                .stream()
                .map(this::getCurrentSnapshot)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stats);
    }

    /**
     * Get comprehensive performance comparison (pre vs post cache)
     */
    @GetMapping("/comparison")
    public ResponseEntity<PerformanceReport> getPerformanceComparison() {
        List<CacheComparison> comparisons = new ArrayList<>();
        long totalHits = 0;
        long totalMisses = 0;

        for (String cacheName : cacheManager.getCacheNames()) {
            CacheSnapshot baseline = baselineSnapshots.get(cacheName);
            CacheSnapshot current = getCurrentSnapshot(cacheName);

            if (baseline == null) {
                baseline = CacheSnapshot.builder()
                        .cacheName(cacheName)
                        .hitCount(0)
                        .missCount(0)
                        .hitRate(0.0)
                        .requestCount(0)
                        .build();
            }

            long hitDelta = current.getHitCount() - baseline.getHitCount();
            long missDelta = current.getMissCount() - baseline.getMissCount();
            double hitRateImprovement = current.getHitRate() - baseline.getHitRate();

            CacheComparison comparison = CacheComparison.builder()
                    .cacheName(cacheName)
                    .baseline(baseline)
                    .current(current)
                    .hitCountDelta(hitDelta)
                    .missCountDelta(missDelta)
                    .hitRateImprovement(hitRateImprovement)
                    .requestsSaved(hitDelta)
                    .performanceGain(hitDelta > 0 ? (double) hitDelta / (hitDelta + missDelta) * 100 : 0)
                    .build();

            comparisons.add(comparison);
            totalHits += current.getHitCount();
            totalMisses += current.getMissCount();
        }

        double overallHitRate = totalHits + totalMisses > 0 ?
                (double) totalHits / (totalHits + totalMisses) : 0.0;

        String summary = generateSummary(totalHits, totalMisses, overallHitRate);
        List<String> topPerformers = getTopPerformingCachesList();
        List<String> recommendations = getCacheRecommendationsList();

        PerformanceReport report = PerformanceReport.builder()
                .reportTime(LocalDateTime.now())
                .cacheComparisons(comparisons)
                .overallHitRate(overallHitRate)
                .totalHits(totalHits)
                .totalMisses(totalMisses)
                .summary(summary)
                .topPerformers(topPerformers)
                .recommendations(recommendations)
                .build();

        return ResponseEntity.ok(report);
    }

    /**
     * Get top performing caches by hit rate
     */
    @GetMapping("/top-performers")
    public ResponseEntity<List<String>> getTopPerformingCaches() {
        List<String> topCaches = cacheManager.getCacheNames().stream()
                .sorted((c1, c2) -> {
                    double hitRate1 = getCurrentSnapshot(c1).getHitRate();
                    double hitRate2 = getCurrentSnapshot(c2).getHitRate();
                    return Double.compare(hitRate2, hitRate1);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(topCaches);
    }

    private List<String> getTopPerformingCachesList() {
        return cacheManager.getCacheNames().stream()
                .sorted((c1, c2) -> {
                    double hitRate1 = getCurrentSnapshot(c1).getHitRate();
                    double hitRate2 = getCurrentSnapshot(c2).getHitRate();
                    return Double.compare(hitRate2, hitRate1);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get cache optimization recommendations
     */
    @GetMapping("/recommendations")
    public ResponseEntity<List<String>> getCacheRecommendations() {
        List<String> recommendations = getCacheRecommendationsList();
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Reset all cache statistics and baseline
     */
    @DeleteMapping("/reset")
    public ResponseEntity<String> resetCacheStats() {
        for (String cacheName : cacheManager.getCacheNames()) {
            var cache = cacheManager.getCache(cacheName);
            if (cache instanceof CaffeineCache caffeineCache) {
                caffeineCache.clear();
            }
        }
        baselineSnapshots.clear();
        return ResponseEntity.ok("All cache statistics reset at " + LocalDateTime.now());
    }

    /**
     * Get cache performance summary
     */
    @GetMapping("/summary")
    public ResponseEntity<String> getPerformanceSummary() {
        long totalHits = 0;
        long totalMisses = 0;
        long totalEvictions = 0;
        double totalResponseTimeSaved = 0;

        for (String cacheName : cacheManager.getCacheNames()) {
            CacheSnapshot snapshot = getCurrentSnapshot(cacheName);
            totalHits += snapshot.getHitCount();
            totalMisses += snapshot.getMissCount();
            totalEvictions += snapshot.getEvictionCount();
            totalResponseTimeSaved += calculateTimeSaved(snapshot);
        }

        double hitRate = totalHits + totalMisses > 0 ?
                (double) totalHits / (totalHits + totalMisses) : 0.0;

        String summary = String.format(
                "🚀 Cache Performance Analysis Summary:\n\n" +
                "📊 Overall Statistics:\n" +
                "   • Hit Rate: %.1f%% (%d hits out of %d requests)\n" +
                "   • Database Queries Saved: %d\n" +
                "   • Estimated Response Time Saved: %.1f ms\n" +
                "   • Cache Evictions: %d\n\n" +
                "💡 Impact:\n" +
                "   • Cache prevented %d database roundtrips\n" +
                "   • Performance improvement: %s\n" +
                "   • System efficiency: %s",
                hitRate * 100, totalHits, totalHits + totalMisses, totalHits,
                totalResponseTimeSaved, totalEvictions, totalHits,
                hitRate > 0.7 ? "Excellent (>70%)" :
                hitRate > 0.5 ? "Good (50-70%)" :
                hitRate > 0.3 ? "Moderate (30-50%)" : "Needs Optimization (<30%)",
                totalHits > 1000 ? "High efficiency - cache is significantly reducing database load" :
                totalHits > 100 ? "Moderate efficiency - cache is providing good benefits" :
                "Low efficiency - consider reviewing caching strategy"
        );

        return ResponseEntity.ok(summary);
    }

    /**
     * Get individual cache analysis
     */
    @GetMapping("/{cacheName}")
    public ResponseEntity<CacheComparison> getCacheAnalysis(@PathVariable String cacheName) {
        if (!cacheManager.getCacheNames().contains(cacheName)) {
            return ResponseEntity.notFound().build();
        }

        CacheSnapshot baseline = baselineSnapshots.get(cacheName);
        CacheSnapshot current = getCurrentSnapshot(cacheName);

        if (baseline == null) {
            baseline = CacheSnapshot.builder()
                    .cacheName(cacheName)
                    .hitCount(0)
                    .missCount(0)
                    .hitRate(0.0)
                    .requestCount(0)
                    .build();
        }

        long hitDelta = current.getHitCount() - baseline.getHitCount();
        long missDelta = current.getMissCount() - baseline.getMissCount();

        CacheComparison comparison = CacheComparison.builder()
                .cacheName(cacheName)
                .baseline(baseline)
                .current(current)
                .hitCountDelta(hitDelta)
                .missCountDelta(missDelta)
                .hitRateImprovement(current.getHitRate() - baseline.getHitRate())
                .requestsSaved(hitDelta)
                .performanceGain(hitDelta > 0 ? (double) hitDelta / (hitDelta + missDelta) * 100 : 0)
                .build();

        return ResponseEntity.ok(comparison);
    }

    private CacheSnapshot getCurrentSnapshot(String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache instanceof CaffeineCache caffeineCache) {
            CacheStats stats = caffeineCache.getNativeCache().stats();

            return CacheSnapshot.builder()
                    .cacheName(cacheName)
                    .hitCount(stats.hitCount())
                    .missCount(stats.missCount())
                    .hitRate(stats.hitRate())
                    .requestCount(stats.requestCount())
                    .loadCount(stats.loadCount())
                    .averageLoadTime(stats.averageLoadPenalty() / 1_000_000.0) // Convert to milliseconds
                    .evictionCount(stats.evictionCount())
                    .timestamp(LocalDateTime.now())
                    .build();
        }
        return CacheSnapshot.builder()
                .cacheName(cacheName)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private List<String> getCacheRecommendationsList() {
        List<String> recommendations = new ArrayList<>();

        for (String cacheName : cacheManager.getCacheNames()) {
            CacheSnapshot snapshot = getCurrentSnapshot(cacheName);

            if (snapshot.getHitRate() < 0.3 && snapshot.getRequestCount() > 10) {
                recommendations.add("⚠️ Cache '" + cacheName + "' has low hit rate (" +
                        String.format("%.1f%%", snapshot.getHitRate() * 100) +
                        "). Consider reviewing cache keys, TTL settings, or caching strategy.");
            }

            if (snapshot.getEvictionCount() > snapshot.getHitCount() * 0.1) {
                recommendations.add("📈 Cache '" + cacheName + "' has high eviction rate (" +
                        snapshot.getEvictionCount() + " evictions). Consider increasing cache size or reducing TTL.");
            }

            if (snapshot.getHitCount() == 0 && snapshot.getRequestCount() > 5) {
                recommendations.add("❌ Cache '" + cacheName + "' is not providing any hits despite " +
                        snapshot.getRequestCount() + " requests. Review caching implementation.");
            }

            if (snapshot.getHitRate() > 0.9 && snapshot.getRequestCount() > 50) {
                recommendations.add("✅ Cache '" + cacheName + "' is performing excellently (" +
                        String.format("%.1f%%", snapshot.getHitRate() * 100) + " hit rate)!");
            }
        }

        if (recommendations.isEmpty()) {
            recommendations.add("🎉 All caches are performing within acceptable ranges!");
        }

        return recommendations;
    }

    private String generateSummary(long totalHits, long totalMisses, double hitRate) {
        long totalRequests = totalHits + totalMisses;
        return String.format(
                "Cache Performance: %.1f%% hit rate (%d/%d requests). " +
                "Cache prevented %d database queries. " +
                "%s",
                hitRate * 100, totalHits, totalRequests, totalHits,
                hitRate > 0.7 ? "Excellent performance! 🚀" :
                hitRate > 0.5 ? "Good performance. 👍" :
                "Consider optimizing cache strategy. ⚠️"
        );
    }

    private double calculateTimeSaved(CacheSnapshot snapshot) {
        // Assume cache hit is ~1ms, database query is ~50ms average
        double cacheHitTime = 1.0;
        double databaseQueryTime = 50.0;
        return snapshot.getHitCount() * (databaseQueryTime - cacheHitTime);
    }
}
