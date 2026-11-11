package com.tayjava.config.security;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * Lesson 28: web chain with form login + {@code oauth2Login} (Google/GitHub). API JWT chain stays
 * separate and stateless.
 */
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final UserDetailsService userDetailsService;
  private final LinkingOAuth2UserService linkingOAuth2UserService;
  private final LinkingOidcUserService linkingOidcUserService;

  @Bean
  @Order(2)
  SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            auth ->
                auth.dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD)
                    .permitAll()
                    .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico")
                    .permitAll()
                    .requestMatchers("/login", "/error", "/oauth2/**", "/login/oauth2/**")
                    .permitAll()
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated())
        .formLogin(form -> form.loginPage("/login").permitAll().defaultSuccessUrl("/", true))
        .oauth2Login(
            oauth2 ->
                oauth2
                    .loginPage("/login")
                    .userInfoEndpoint(
                        userInfo ->
                            userInfo
                                .userService(linkingOAuth2UserService)
                                .oidcUserService(linkingOidcUserService))
                    .defaultSuccessUrl("/", true))
        .rememberMe(
            remember ->
                remember
                    .key("tayjava-lab-remember-me-demo-only")
                    .userDetailsService(userDetailsService)
                    .tokenValiditySeconds(86400))
        .sessionManagement(
            sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .sessionFixation(sf -> sf.changeSessionId())
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(false))
        .logout(
            logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "remember-me")
                    .permitAll())
        .cors(withDefaults())
        .headers(
            headers ->
                headers
                    .contentSecurityPolicy(
                        csp ->
                            csp.policyDirectives(
                                "default-src 'self'; style-src 'self' 'unsafe-inline'"))
                    .frameOptions(frame -> frame.sameOrigin()));
    return http.build();
  }

  @Bean
  HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }
}
