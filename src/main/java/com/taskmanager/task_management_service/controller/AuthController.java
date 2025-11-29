package com.taskmanager.task_management_service.controller;

import com.taskmanager.task_management_service.entity.User;
import com.taskmanager.task_management_service.enums.Role;
import com.taskmanager.task_management_service.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            // Проверка надежности пароля
            if (!isPasswordStrong(request.getPassword())) {
                return ResponseEntity.badRequest().body("Password does not meet security requirements: " +
                        "minimum 8 characters, at least one uppercase letter, one lowercase letter, " +
                        "one digit and one special character");
            }

            // Конвертируем строку в enum Role
            Role role = convertStringToRole(request.getRole());

            User user = authService.registerUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    role
            );

            // Не возвращаем пароль в ответе
            user.setPassword(null);

            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Метод для проверки надежности пароля
    private boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        // Проверка на наличие спецсимволов, цифр, букв в верхнем и нижнем регистре
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }

    // Метод для конвертации строки в Role
    private Role convertStringToRole(String roleString) {
        if (roleString == null) {
            return Role.USER; // По умолчанию USER
        }

        switch (roleString.toUpperCase()) {
            case "ADMIN":
            case "АДМИНИСТРАТОР":
                return Role.ADMIN;
            case "USER":
            case "ПОЛЬЗОВАТЕЛЬ":
            default:
                return Role.USER;
        }
    }

    // DTO для запроса регистрации
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
        private String role; // Теперь String вместо Role

        // Геттеры и сеттеры
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}