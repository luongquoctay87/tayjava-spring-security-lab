package com.tayjava.service;

import com.tayjava.common.dto.auth.TokenResp;
import com.tayjava.config.security.JwtProperties;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/**
 * Lesson 23: issue HS256 access tokens. Roles go into claim {@code roles} (no {@code ROLE_}
 * prefix); fine-grained permissions into claim {@code permissions} for the JWT filter lesson.
 */
@Service
@RequiredArgsConstructor
public class AccessTokenService {

  private final JwtEncoder jwtEncoder;
  private final JwtProperties properties;

  public TokenResp issue(Authentication authentication) {
    Instant now = Instant.now();
    Instant expiresAt = now.plus(properties.accessTokenTtl());

    List<String> roles =
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(a -> a.startsWith("ROLE_"))
            .map(a -> a.substring("ROLE_".length()))
            .toList();

    List<String> permissions =
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(a -> !a.startsWith("ROLE_"))
            .toList();

    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuer(properties.issuer())
            .subject(authentication.getName())
            .audience(List.of(properties.audience()))
            .issuedAt(now)
            .expiresAt(expiresAt)
            .id(UUID.randomUUID().toString())
            .claim("roles", roles)
            .claim("permissions", permissions)
            .build();

    // No kid: ImmutableSecret has a single key; a kid header makes Nimbus fail key selection.
    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
    String value =
        jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    return TokenResp.bearer(value, properties.accessTokenTtl().toSeconds());
  }
}
