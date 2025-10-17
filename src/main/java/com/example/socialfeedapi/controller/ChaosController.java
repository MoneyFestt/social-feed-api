package com.example.socialfeedapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
public class ChaosController {

    private static final AtomicBoolean chaosEnabled = new AtomicBoolean(false);
    private static final Random random = new Random();

    @GetMapping("/api/chaos/enable")
    public String enableChaos() {
        chaosEnabled.set(true);
        return "Chaos mode enabled";
    }

    @GetMapping("/api/chaos/disable")
    public String disableChaos() {
        chaosEnabled.set(false);
        return "Chaos mode disabled";
    }

    // Метод для перевірки перед кожним запитом
    public static void maybeThrowChaos() {
        if (chaosEnabled.get() && random.nextDouble() < 0.1) { // 10% шанс помилки
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Chaos monkey strike!");
        }
    }
}
