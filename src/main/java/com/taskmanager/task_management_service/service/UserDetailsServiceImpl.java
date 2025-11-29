package com.taskmanager.task_management_service.service;

import com.taskmanager.task_management_service.entity.User;
import com.taskmanager.task_management_service.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // ДЕБАГ - смотрим что реально в базе
        System.out.println("=== DEBUG UserDetails ===");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Role from DB: " + user.getRole());
        System.out.println("Role name(): " + user.getRole().name());
        System.out.println("Role getRussianValue(): " + user.getRole().getRussianValue());
        System.out.println("========================");

        String roleName = user.getRole().name();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }
}