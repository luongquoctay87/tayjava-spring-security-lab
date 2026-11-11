package com.tayjava.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

/**
 * Spring Authorization Server demo — protocol chain + form-login chain.
 * Lab side branch; not merged into the Auth API project line.
 */
@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {

  /** Chain 1 — protocol endpoints only: /oauth2/**, /.well-known/**, /userinfo, /connect/logout. */
  @Bean
  @Order(1)
  SecurityFilterChain authorizationServerChain(HttpSecurity http) throws Exception {
    // Boot 4 / Security 7 / SAS 2.x: oauth2AuthorizationServer DSL
    return http.oauth2AuthorizationServer(
            authorizationServer -> {
              http.securityMatcher(authorizationServer.getEndpointsMatcher());
              authorizationServer.oidc(Customizer.withDefaults());
            })
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .exceptionHandling(
            ex ->
                ex.defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint("/login"),
                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
        .oauth2ResourceServer(rs -> rs.jwt(Customizer.withDefaults()))
        .build();
  }

  /** Chain 2 — login page for authorization_code exercises. */
  @Bean
  @Order(100)
  SecurityFilterChain defaultChain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/error", "/actuator/health")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .formLogin(Customizer.withDefaults())
        .build();
  }
}
