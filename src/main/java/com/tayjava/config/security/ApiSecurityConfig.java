package com.tayjava.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/** Lesson 10: API chain matched on /api/** (Order 1). */
@Configuration
public class ApiSecurityConfig {

  @Bean
  @Order(1)
  SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/**")
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/auth/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/courses", "/api/courses/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/courses")
                    .hasAnyRole("INSTRUCTOR", "ADMIN")
                    .requestMatchers("/api/enrollments/**", "/api/me")
                    .authenticated()
                    .anyRequest()
                    .authenticated())
        .csrf(
            csrf ->
                csrf.disable()) // Stateless API: no browser cookie session — CSRF N/A (lesson 15)
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(Customizer.withDefaults());
    return http.build();
  }
}
