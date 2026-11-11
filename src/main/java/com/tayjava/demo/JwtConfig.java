package com.tayjava.demo;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.util.Assert;

/**
 * HS256 encoder/decoder for the lesson-22 JWT playground.
 * Secret comes from env {@code TAYJAVA_JWT_SECRET} via {@code tayjava.jwt.secret}.
 */
@Configuration
public class JwtConfig {

  static final String KEY_ID = "tj-2026-01";

  @Value("${tayjava.jwt.secret}")
  private String secret;

  private byte[] secretBytes() {
    byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
    Assert.isTrue(
        bytes.length >= 32,
        "tayjava.jwt.secret must be at least 256 bits (32 UTF-8 bytes); use openssl rand -base64 32");
    return bytes;
  }

  @Bean
  JwtEncoder jwtEncoder() {
    OctetSequenceKey jwk =
        new OctetSequenceKey.Builder(secretBytes()).keyID(KEY_ID).build();
    return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(jwk)));
  }

  @Bean
  JwtDecoder jwtDecoder() {
    SecretKey key = new SecretKeySpec(secretBytes(), "HmacSHA256");
    return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
  }
}
