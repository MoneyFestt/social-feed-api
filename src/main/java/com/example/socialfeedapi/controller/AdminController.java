package com.example.socialfeedapi.controller;

import com.example.socialfeedapi.service.DataSeederService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AdminController {

    private final DataSeederService dataSeederService;

    public AdminController(DataSeederService dataSeederService) {
        this.dataSeederService = dataSeederService;
    }

    @PostMapping("/seed")
    public String seedDatabase() {
        dataSeederService.seedData(); // Виклик асинхронного методу
        return "Генерація тестових даних запущена у фоновому режимі!";
    }
}
