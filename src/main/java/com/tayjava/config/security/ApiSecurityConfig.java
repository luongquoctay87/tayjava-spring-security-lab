package com.tayjava.config.security;

import static com.tayjava.config.security.Authorities.Perm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Lesson 17: action rules use {@code hasAuthority(permission)}; area rules keep {@code hasRole}.
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
                    .hasAuthority(Perm.COURSE_DELETE)
                    .requestMatchers(HttpMethod.POST, "/api/courses")
                    .hasAuthority(Perm.COURSE_CREATE)
                    .requestMatchers(HttpMethod.PUT, "/api/courses/*")
                    .hasAuthority(Perm.COURSE_UPDATE_OWN)
                    .requestMatchers(HttpMethod.GET, "/api/enrollments/me")
                    .hasAuthority(Perm.ENROLLMENT_READ_OWN)
                    .requestMatchers(HttpMethod.GET, "/api/reports/**")
                    .hasAuthority(Perm.REPORT_VIEW)
                    .requestMatchers("/api/me")
                    .authenticated()
                    .anyRequest()
                    .authenticated())
        .csrf(csrf -> csrf.disable()) // Stateless API: no browser cookie session — CSRF N/A
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(Customizer.withDefaults());
    return http.build();
  }
}
