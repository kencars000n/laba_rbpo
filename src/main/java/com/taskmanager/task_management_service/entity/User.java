package com.taskmanager.task_management_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taskmanager.task_management_service.enums.Role;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    // НОВОЕ ПОЛЕ: пароль для аутентификации
    @Column(nullable = false)
    @JsonIgnore // Не показываем пароль в JSON ответах
    private String password;

    @OneToMany(mappedBy = "assignee")
    @JsonIgnore
    private List<Task> assignedTasks = new ArrayList<>();

    // Конструкторы
    public User() {}

    // Старый конструктор (для обратной совместимости)
    public User(String username, String email, Role role) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.password = "default"; // временное значение
    }

    // Новый конструктор с паролем
    public User(String username, String email, Role role, String password) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<Task> getAssignedTasks() { return assignedTasks; }
    public void setAssignedTasks(List<Task> assignedTasks) { this.assignedTasks = assignedTasks; }
}