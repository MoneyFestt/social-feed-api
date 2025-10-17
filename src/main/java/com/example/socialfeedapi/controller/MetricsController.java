package com.example.socialfeedapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class MetricsController {

    // Статистика
    private static final AtomicInteger totalRequests = new AtomicInteger(0);
    private static final AtomicInteger errorCount = new AtomicInteger(0);
    private static final AtomicLong totalResponseTimeMs = new AtomicLong(0);

    // Метод для оновлення статистики (будемо викликати у filter)
    public static void recordRequest(long durationMs, boolean isError) {
        totalRequests.incrementAndGet();
        totalResponseTimeMs.addAndGet(durationMs);
        if (isError) {
            errorCount.incrementAndGet();
        }
    }

    @GetMapping("/api/metrics")
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        int requests = totalRequests.get();
        metrics.put("total_requests", requests);
        metrics.put("average_response_time", requests == 0 ? 0 : totalResponseTimeMs.get() / requests);
        metrics.put("error_rate", requests == 0 ? 0 : (double) errorCount.get() / requests);
        // Можна додати інші метрики пізніше (active_connections, cache_hit_rate)
        return metrics;
    }
}
