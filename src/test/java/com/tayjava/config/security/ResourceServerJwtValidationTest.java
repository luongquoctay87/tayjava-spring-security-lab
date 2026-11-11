package com.tayjava.config.security;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ResourceServerJwtValidationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private JwtDecoder jwtDecoder;
  @Autowired private JwtProperties jwtProperties;

  @Test
  void selfIssuedLoginTokenIsAcceptedByResourceServer() throws Exception {
    String token = login("an.student", "demo1234");
    mockMvc
        .perform(get("/api/enrollments/me").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("an.student"));
  }

  @Test
  void tokenWithWrongAudienceIsRejected() {
    SecretKeySpec key =
        new SecretKeySpec(
            jwtProperties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
    Instant now = Instant.now();
    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuer(jwtProperties.issuer())
            .subject("an.student")
            .audience(List.of("not-tayjava-api"))
            .issuedAt(now)
            .expiresAt(now.plusSeconds(60))
            .claim("roles", List.of("STUDENT"))
            .build();
    String token =
        encoder
            .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
            .getTokenValue();

    assertThatThrownBy(() -> jwtDecoder.decode(token)).isInstanceOf(BadJwtException.class);
  }

  private String login(String username, String password) throws Exception {
    return AuthLoginSupport.accessToken(mockMvc, username, password);
  }
}
