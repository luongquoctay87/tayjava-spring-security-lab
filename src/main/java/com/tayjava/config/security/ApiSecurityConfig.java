package com.tayjava.config.security;

import static com.tayjava.config.security.Authorities.Perm;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * Lesson 21: reinforce {@code STATELESS} for {@code /api/**} — no session, no request cache,
 * plain 401 (JWT login arrives in the next lessons).
 */
@Configuration
@RequiredArgsConstructor
public class ApiSecurityConfig {

  private final EnrolledCourseAuthorizationManager enrolledCourseAuthorizationManager;

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
                    .requestMatchers(HttpMethod.GET, "/api/courses/{courseId}/lectures")
                    .access(enrolledCourseAuthorizationManager)
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
        // 1. SecurityContext lives only for this request — nothing in HttpSession
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // 2. API clients do not redirect-after-login
        .requestCache(cache -> cache.disable())
        // 3. Safe while auth is Authorization header / httpBasic — not cookie session
        .csrf(csrf -> csrf.disable())
        .httpBasic(Customizer.withDefaults())
        .exceptionHandling(
            ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
    return http.build();
  }
}
