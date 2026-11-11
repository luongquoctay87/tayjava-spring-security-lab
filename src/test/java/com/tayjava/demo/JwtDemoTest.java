package com.tayjava.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

@SpringBootTest
class JwtDemoTest {

  @Autowired private JwtDemo jwtDemo;
  @Autowired private JwtDecoder jwtDecoder;

  private static String b64(String json) {
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(json.getBytes(StandardCharsets.UTF_8));
  }

  @Test
  void validTokenDecodesWithExpectedClaims() {
    String token = jwtDemo.issueForDemo("42", List.of("INSTRUCTOR")).value();

    var jwt = jwtDecoder.decode(token);

    assertThat(jwt.getSubject()).isEqualTo("42");
    assertThat(jwt.getIssuer()).hasToString("https://tayjava.com");
    assertThat(jwt.getAudience()).containsExactly("tayjava-api");
    assertThat(jwt.getClaimAsStringList("roles")).containsExactly("INSTRUCTOR");
    assertThat(jwt.getId()).isNotBlank();
    assertThat(jwt.getExpiresAt()).isAfter(jwt.getIssuedAt());
  }

  @Test
  void oneCharacterChangeInSignatureFailsVerify() {
    String token = jwtDemo.issueForDemo("42", List.of("INSTRUCTOR")).value();
    String[] parts = token.split("\\.");
    char last = parts[2].charAt(parts[2].length() - 1);
    char flipped = (last == 'A') ? 'B' : 'A';
    String tampered =
        parts[0] + "." + parts[1] + "." + parts[2].substring(0, parts[2].length() - 1) + flipped;

    assertThatThrownBy(() -> jwtDecoder.decode(tampered)).isInstanceOf(JwtException.class);
  }

  @Test
  void tamperedPayloadGrantingAdminFailsVerify() {
    String token = jwtDemo.issueForDemo("42", List.of("STUDENT")).value();
    String[] parts = token.split("\\.");
    String evil =
        b64(
            """
            {"iss":"https://tayjava.com","sub":"42","aud":["tayjava-api"],\
            "exp":9999999999,"roles":["ADMIN"]}""");

    assertThatThrownBy(() -> jwtDecoder.decode(parts[0] + "." + evil + "." + parts[2]))
        .isInstanceOf(JwtException.class);
  }

  @Test
  void algNoneIsRejected() {
    String forged =
        b64("{\"alg\":\"none\"}")
            + "."
            + b64("{\"sub\":\"42\",\"roles\":[\"ADMIN\"],\"exp\":9999999999}")
            + ".";

    assertThatThrownBy(() -> jwtDecoder.decode(forged)).isInstanceOf(JwtException.class);
  }
}
