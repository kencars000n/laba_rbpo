package com.taskmanager.task_management_service.config;

import com.taskmanager.task_management_service.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(requestHandler)
                )
                .authorizeHttpRequests(authz -> authz
                        // ВРЕМЕННО - разрешаем GET /api/tasks всем аутентифицированным
                        .requestMatchers("GET", "/api/tasks").authenticated()

                        // Публичные endpoints
                        .requestMatchers("/api/health", "/api/auth/register").permitAll()

                        // ===== USERS MANAGEMENT =====
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        // ===== PROJECTS STATISTICS =====
                        .requestMatchers("/api/projects/*/statistics").authenticated()

                        // ===== PROJECTS MANAGEMENT =====
                        .requestMatchers("/api/projects/**").hasRole("ADMIN")

                        // ===== TASKS SEARCH =====
                        .requestMatchers("/api/tasks/search/**").authenticated()

                        // ===== TASKS MANAGEMENT =====
                        .requestMatchers("DELETE", "/api/tasks/**").hasRole("ADMIN")
                        .requestMatchers("/api/tasks/**").authenticated()

                        // ===== ВСЕ ОСТАЛЬНЫЕ ENDPOINTS =====
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}