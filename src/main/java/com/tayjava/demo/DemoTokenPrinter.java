package com.tayjava.demo;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/** Dev-only token printer for curl exercises. */
@Configuration
@Profile("dev")
@Slf4j(topic = "DemoTokenPrinter")
public class DemoTokenPrinter {

  @Bean
  ApplicationRunner printToken(@Value("${tayjava.jwt.secret}") String secret) {
    return args -> {
      var claims =
          new JWTClaimsSet.Builder()
              .subject("bao.teacher")
              .claim("scope", "courses.read")
              .issueTime(new Date())
              .expirationTime(Date.from(Instant.now().plus(Duration.ofHours(1))))
              .build();
      var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
      jwt.sign(new MACSigner(secret.getBytes(StandardCharsets.UTF_8)));
      log.info("DEMO_TOKEN={}", jwt.serialize());
    };
  }
}
