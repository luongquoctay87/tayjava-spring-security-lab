package com.tayjava.config.security;

import static com.tayjava.config.security.Authorities.Perm;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Lesson 24: {@code oauth2ResourceServer().jwt()} registers {@code BearerTokenAuthenticationFilter}
 * on the {@code /api/**} chain. API clients authenticate with {@code Authorization: Bearer}.
 */
@Configuration
@RequiredArgsConstructor
public class ApiSecurityConfig {

  private final EnrolledCourseAuthorizationManager enrolledCourseAuthorizationManager;
  private final JwtAuthenticationConverter jwtAuthenticationConverter;

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
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .requestCache(cache -> cache.disable())
        .csrf(csrf -> csrf.disable())
        .oauth2ResourceServer(
            oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
    return http.build();
  }
}
