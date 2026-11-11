package com.tayjava.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Lesson 06: single {@link SecurityFilterChain} with lambda DSL.
 * Later lessons split chains, swap UserDetailsService, and tighten CSRF/session.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/auth/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/courses", "/api/courses/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/courses")
                    .hasAnyRole("INSTRUCTOR", "ADMIN")
                    .requestMatchers("/api/enrollments/**")
                    .authenticated()
                    .anyRequest()
                    .authenticated())
        .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) // lesson-15 covers CSRF in depth
        .httpBasic(Customizer.withDefaults())
        .formLogin(Customizer.withDefaults());
    return http.build();
  }

  @Bean
  UserDetailsService userDetailsService() {
    // {noop} until lesson-09 introduces PasswordEncoder / BCrypt.
    return new InMemoryUserDetailsManager(
        User.withUsername("an.student").password("{noop}demo1234").roles("STUDENT").build(),
        User.withUsername("bao.teacher").password("{noop}demo1234").roles("INSTRUCTOR").build(),
        User.withUsername("root.admin").password("{noop}demo1234").roles("ADMIN").build());
  }
}
