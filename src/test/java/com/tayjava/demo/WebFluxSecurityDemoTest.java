package com.tayjava.demo;

import static org.assertj.core.api.Assertions.assertThat;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tayjava.TayJavApplication;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
    classes = TayJavApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebFluxSecurityDemoTest {

  @LocalServerPort private int port;
  @Autowired private ApplicationContext applicationContext;

  @Value("${tayjava.jwt.secret}")
  private String secret;

  private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
    webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
  }

  @Test
  void contextLoadsReactiveSecurity() {
    assertThat(applicationContext.getBeanNamesForType(SecurityWebFilterChain.class)).isNotEmpty();
  }

  @Test
  void publicEndpointIsOpen() {
    webTestClient
        .get()
        .uri("/api/public")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo("anyone can call");
  }

  @Test
  void protectedEndpointWithoutTokenReturns401() {
    webTestClient.get().uri("/api/me/context").exchange().expectStatus().isUnauthorized();
  }

  @Test
  void annotationAndReactiveContextReturnSubject() throws Exception {
    String token = mintToken("bao.teacher");

    webTestClient
        .get()
        .uri("/api/me/annotation")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.method")
        .isEqualTo("@AuthenticationPrincipal")
        .jsonPath("$.principal")
        .isEqualTo("bao.teacher");

    webTestClient
        .get()
        .uri("/api/me/context")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.method")
        .isEqualTo("ReactiveSecurityContextHolder")
        .jsonPath("$.principal")
        .isEqualTo("bao.teacher");
  }

  @Test
  void threadLocalSecurityContextIsEmptyOnWebFlux() throws Exception {
    String token = mintToken("bao.teacher");

    webTestClient
        .get()
        .uri("/api/me/threadlocal")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.principal")
        .isEqualTo("null - THIS IS THE PROBLEM");
  }

  private String mintToken(String subject) throws Exception {
    var claims =
        new JWTClaimsSet.Builder()
            .subject(subject)
            .claim("scope", "courses.read")
            .issueTime(new Date())
            .expirationTime(Date.from(Instant.now().plus(Duration.ofHours(1))))
            .build();
    var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
    jwt.sign(new MACSigner(secret.getBytes(StandardCharsets.UTF_8)));
    return jwt.serialize();
  }
}
