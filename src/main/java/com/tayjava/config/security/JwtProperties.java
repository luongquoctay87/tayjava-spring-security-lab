package com.tayjava.config.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Lesson 23: HS256 signing material and access-token lifetime.
 *
 * <p>{@code secret} must come from env {@code TAYJAVA_JWT_SECRET} (no in-repo default).
 */
@Validated
@ConfigurationProperties(prefix = "tayjava.jwt")
public record JwtProperties(
    @NotBlank @Size(min = 32) String secret,
    @NotBlank String issuer,
    @NotBlank String audience,
    Duration accessTokenTtl) {}
