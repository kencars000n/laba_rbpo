package com.taskmanager.task_management_service.repository;

import com.taskmanager.task_management_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Найти пользователя по username
    Optional<User> findByUsername(String username);

    // Найти пользователя по email
    Optional<User> findByEmail(String email);

    // Проверить существование пользователя по username
    boolean existsByUsername(String username);

    // Проверить существование пользователя по email
    boolean existsByEmail(String email);
}