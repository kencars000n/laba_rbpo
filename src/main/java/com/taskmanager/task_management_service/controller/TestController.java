package com.taskmanager.task_management_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")

public class TestController {

    @GetMapping("/health")
    public String health() {
        return "Service is running! Proper package structure created.";
    }
}