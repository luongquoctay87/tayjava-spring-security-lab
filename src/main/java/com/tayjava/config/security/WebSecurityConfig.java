package com.tayjava.config.security;

import com.tayjava.service.DemoUserDetailsService;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * Lesson 16: web chain with explicit public resources and {@code /admin/**} locked to ADMIN.
 */
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final DemoUserDetailsService userDetailsService;

  @Bean
  @Order(2)
  SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            auth ->
                auth.dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD)
                    .permitAll()
                    .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico")
                    .permitAll()
                    .requestMatchers("/login", "/error")
                    .permitAll()
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
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
        .logout(
            logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "remember-me")
                    .permitAll());
    return http.build();
  }

  @Bean
  HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }
}
