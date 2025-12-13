package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Привет из HelloController!";
    }

    @GetMapping("/")
    public String home() {
        return "Добро пожаловать в Spring Boot приложение! Доступные эндпоинты: /hello, /greeting, /welcome";
    }
}