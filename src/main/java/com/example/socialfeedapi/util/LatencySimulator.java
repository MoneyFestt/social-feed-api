package com.example.socialfeedapi.util;

public class LatencySimulator {

    public static void simulateDbLatency(String operationType) {
        try {
            if ("read".equalsIgnoreCase(operationType)) {
                Thread.sleep(50); // 50ms для читання
            } else {
                Thread.sleep(100); // 100ms для запису
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}