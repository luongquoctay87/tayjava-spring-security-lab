package com.tayjava.config.security;

import static com.tayjava.config.security.Authorities.Perm;

import com.tayjava.service.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Lesson 32: API chain with Bearer JWT plus MFA verify; {@link ApiAuthenticationEntryPoint} /
 * {@link ApiAccessDeniedHandler} writing the {@code Resp} envelope for 401/403.
 */
@Configuration
@RequiredArgsConstructor
public class ApiSecurityConfig {

  private final EnrolledCourseAuthorizationManager enrolledCourseAuthorizationManager;
  private final JwtAuthenticationConverter jwtAuthenticationConverter;
  private final ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;
  private final ApiAccessDeniedHandler apiAccessDeniedHandler;
  private final LoginAttemptService loginAttemptService;

  @Bean
  @Order(1)
  SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/**")
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        HttpMethod.POST,
                        "/api/auth/login",
                        "/api/auth/mfa/verify",
                        "/api/auth/refresh",
                        "/api/auth/logout")
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
        .cors(withDefaults())
        .headers(
            headers ->
                headers
                    .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
                    .frameOptions(frame -> frame.sameOrigin())
                    .referrerPolicy(
                        ref ->
                            ref.policy(
                                org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter
                                    .ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)))
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(apiAuthenticationEntryPoint)
                    .accessDeniedHandler(apiAccessDeniedHandler))
        .oauth2ResourceServer(
            oauth2 ->
                oauth2
                    .authenticationEntryPoint(apiAuthenticationEntryPoint)
                    .accessDeniedHandler(apiAccessDeniedHandler)
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
        .addFilterBefore(new LoginRateLimitFilter(loginAttemptService), BearerTokenAuthenticationFilter.class)
        .addFilterAfter(new TenantContextFilter(), BearerTokenAuthenticationFilter.class);
    return http.build();
  }
}
