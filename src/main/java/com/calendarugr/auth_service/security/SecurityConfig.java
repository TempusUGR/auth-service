package com.calendarugr.auth_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Desactiva CSRF
                .cors(cors -> cors.disable())  // Desactiva CORS
                .authorizeHttpRequests(requests -> requests
                                .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/refresh").permitAll() // Rutas públicas
                                .anyRequest().permitAll()  // Permite todas las demás solicitudes
                )
                .httpBasic(basic -> basic.disable()) // Desactiva autenticación básica
                .formLogin(login -> login.disable()) // Desactiva login basado en formularios
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Desactiva sesiones

        return http.build();
    }
}