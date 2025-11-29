package com.taskmanager.task_management_service.service;

import com.taskmanager.task_management_service.entity.User;
import com.taskmanager.task_management_service.enums.Role;
import com.taskmanager.task_management_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Регулярное выражение для проверки пароля: минимум 8 символов, хотя бы одна цифра и один специальный символ
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String email, String password, Role role) {
        // Проверка уникальности username и email
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        // Проверка надежности пароля
        if (!isPasswordValid(password)) {
            throw new RuntimeException("Password does not meet security requirements. " +
                    "Password must be at least 8 characters long and contain at least one digit and one special character");
        }

        // Создание нового пользователя
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(password)); // Хешируем пароль

        return userRepository.save(user);
    }

    private boolean isPasswordValid(String password) {
        return pattern.matcher(password).matches();
    }
}