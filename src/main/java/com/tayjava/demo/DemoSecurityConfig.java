package com.tayjava.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Side-branch security for the JWT structure playground.
 * Permits the learning token endpoint; everything else stays authenticated.
 */
@Configuration
@EnableWebSecurity
public class DemoSecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.GET, "/api/debug/token")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
        .httpBasic(Customizer.withDefaults());
    return http.build();
  }
}
