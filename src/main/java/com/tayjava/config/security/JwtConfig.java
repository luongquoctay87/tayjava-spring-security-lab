package com.tayjava.config.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;

/**
 * Self-issued HS256 JWT (default). Active unless profile {@code external-jwt} is enabled.
 */
@Configuration
@Profile("!external-jwt")
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

  private final JwtProperties properties;

  public JwtConfig(JwtProperties properties) {
    this.properties = properties;
  }

  private SecretKeySpec secretKey() {
    return new SecretKeySpec(
        properties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
  }

  @Bean
  JwtEncoder jwtEncoder() {
    return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey()));
  }

  @Bean
  JwtDecoder jwtDecoder() {
    NimbusJwtDecoder decoder =
        NimbusJwtDecoder.withSecretKey(secretKey()).macAlgorithm(MacAlgorithm.HS256).build();

    OAuth2TokenValidator<Jwt> withIssuer =
        JwtValidators.createDefaultWithIssuer(properties.issuer());
    OAuth2TokenValidator<Jwt> withAudience =
        new JwtClaimValidator<List<String>>(
            JwtClaimNames.AUD, aud -> aud != null && aud.contains(properties.audience()));
    decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience));
    return decoder;
  }
}
