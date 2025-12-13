package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class InfoController {

    @GetMapping("/info")
    public String getInfo() {
        return "Информация о приложении: Spring Boot Demo";
    }

    @GetMapping("/time")
    public String getTime() {
        return "Текущее время: " + LocalDateTime.now();
    }

    @GetMapping("/status")
    public String getStatus() {
        return "Статус: Приложение работает нормально ✅";
    }

    @GetMapping("/about")
    public String about() {
        return "Это простое Spring Boot приложение с REST API";
    }
}