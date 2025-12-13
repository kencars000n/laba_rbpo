package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "Мир") String name) {
        return "Приветствие от GreetingController: Здравствуй, " + name + "!";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Добро пожаловать в наше Spring Boot приложение!";
    }
}