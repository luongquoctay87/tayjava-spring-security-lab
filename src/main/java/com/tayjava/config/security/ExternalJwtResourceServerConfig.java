package com.tayjava.config.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * Lesson 29: Resource Server mode that validates JWTs from an external Authorization Server via
 * {@code issuer-uri} discovery (JWKS). Activate with {@code --spring.profiles.active=external-jwt}.
 *
 * <p>Self-issued HS256 login stays on the default profile — do not enable {@code external-jwt} for
 * the built-in {@code POST /api/auth/login} flow.
 */
@Configuration
@Profile("external-jwt")
@EnableConfigurationProperties(JwtProperties.class)
public class ExternalJwtResourceServerConfig {

  @Bean
  JwtDecoder jwtDecoder(
      @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuer,
      JwtProperties properties) {
    NimbusJwtDecoder decoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuer);
    OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
    OAuth2TokenValidator<Jwt> withAudience =
        new JwtClaimValidator<List<String>>(
            JwtClaimNames.AUD, aud -> aud != null && aud.contains(properties.audience()));
    decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience));
    return decoder;
  }
}
