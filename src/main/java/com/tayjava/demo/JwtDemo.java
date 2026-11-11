package com.tayjava.demo;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/**
 * Throwaway JWT issuer for lesson 22 — replaced by AccessTokenService on the project line.
 */
@Service
@RequiredArgsConstructor
public class JwtDemo {

  private static final Duration TTL = Duration.ofMinutes(15);

  private final JwtEncoder jwtEncoder;

  public record DemoToken(String value, long expiresIn) {}

  public DemoToken issueForDemo(String userId, List<String> roles) {
    Instant now = Instant.now();
    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuer("https://tayjava.com")
            .subject(userId)
            .audience(List.of("tayjava-api"))
            .issuedAt(now)
            .expiresAt(now.plus(TTL))
            .id(UUID.randomUUID().toString())
            .claim("roles", roles)
            .build();

    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).keyId(JwtConfig.KEY_ID).build();
    String value = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    return new DemoToken(value, TTL.toSeconds());
  }
}
