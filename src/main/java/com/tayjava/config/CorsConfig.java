package com.tayjava.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/** Lesson 36: single CorsConfigurationSource — do not scatter @CrossOrigin. */
@Configuration
public class CorsConfig {

  @Bean
  CorsConfigurationSource corsConfigurationSource(
      @Value("${tayjava.cors.allowed-origins:http://localhost:5173}") List<String> allowedOrigins) {
    CorsConfiguration api = new CorsConfiguration();
    api.setAllowedOrigins(allowedOrigins);
    api.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    api.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
    api.setExposedHeaders(List.of("X-RateLimit-Remaining", "Retry-After"));
    api.setAllowCredentials(true);
    api.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", api);
    return source;
  }
}
