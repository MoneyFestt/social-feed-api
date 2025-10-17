package com.example.socialfeedapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final long serverStartTime;
    private int requestCount = 0;

    public HealthController() {
        this.serverStartTime = System.currentTimeMillis();
    }

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        requestCount++;

        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("server_id", System.getenv().getOrDefault("SERVER_ID", "unknown"));
        response.put("uptime_seconds", (System.currentTimeMillis() - serverStartTime) / 1000);
        response.put("request_count", requestCount);

        return response;
    }
}
