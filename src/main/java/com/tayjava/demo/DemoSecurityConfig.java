package com.tayjava.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Resource-server wiring for Keycloak role mapping.
 * Default profile uses a local HS256 decoder so {@code ./mvnw test} works without Docker.
 * Profile {@code keycloak} documents the real issuer-uri path in README / application-keycloak.yml.
 */
@Configuration
@EnableWebSecurity
public class DemoSecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http, Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter)
      throws Exception {
    http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/public")
                    .permitAll()
                    .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/api/**")
                    .authenticated()
                    .anyRequest()
                    .authenticated())
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
    return http.build();
  }

  @Bean
  Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter(
      @Value("${tayjava.keycloak.client-id}") String clientId) {
    return new KeycloakJwtAuthenticationConverter(clientId, "preferred_username");
  }

  /**
   * Lab decoder so gate tests run offline. Against a live Keycloak, switch to profile
   * {@code keycloak} (issuer-uri) instead of this symmetric key.
   */
  @Bean
  JwtDecoder jwtDecoder(@Value("${tayjava.jwt.secret}") String secret) {
    var key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
  }
}
