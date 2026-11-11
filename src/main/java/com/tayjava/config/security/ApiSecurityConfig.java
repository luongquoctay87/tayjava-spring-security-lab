package com.tayjava.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Lesson 16: ordered {@code authorizeHttpRequests} matchers for the API matrix.
 *
 * <p>Order matters: public GETs and auth endpoints first, then narrow privileged
 * mutations, then authenticated groups, then {@code anyRequest()}.
 */
@Configuration
public class ApiSecurityConfig {

  @Bean
  @Order(1)
  SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/**")
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/refresh")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/courses", "/api/courses/*")
                    .permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/api/courses/*")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/courses")
                    .hasAnyRole("INSTRUCTOR", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/courses/*")
                    .hasAnyRole("INSTRUCTOR", "ADMIN")
                    .requestMatchers("/api/enrollments/**", "/api/me")
                    .authenticated()
                    .anyRequest()
                    .authenticated())
        .csrf(csrf -> csrf.disable()) // Stateless API: no browser cookie session — CSRF N/A
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(Customizer.withDefaults());
    return http.build();
  }
}
