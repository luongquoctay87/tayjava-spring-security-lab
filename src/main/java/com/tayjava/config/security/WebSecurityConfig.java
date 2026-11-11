package com.tayjava.config.security;

import com.tayjava.service.DemoUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/** Lesson 13: remember-me on the web chain (demo key — never use in production). */
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final DemoUserDetailsService userDetailsService;

  @Bean
  @Order(2)
  SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/css/**", "/js/**", "/login", "/error")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .formLogin(form -> form.loginPage("/login").permitAll().defaultSuccessUrl("/", true))
        .rememberMe(
            remember ->
                remember
                    .key("tayjava-lab-remember-me-demo-only")
                    .userDetailsService(userDetailsService)
                    .tokenValiditySeconds(86400))
        .sessionManagement(
            sm ->
                sm.sessionFixation(sf -> sf.changeSessionId())
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(false))
        .logout(Customizer.withDefaults());
    return http.build();
  }

  @Bean
  HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }
}
