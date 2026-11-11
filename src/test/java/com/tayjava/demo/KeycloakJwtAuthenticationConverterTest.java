package com.tayjava.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class KeycloakJwtAuthenticationConverterTest {

  private final KeycloakJwtAuthenticationConverter converter =
      new KeycloakJwtAuthenticationConverter("tayjava-api", "preferred_username");

  @Test
  void mapsRealmAndClientRolesToAuthorities() {
    Jwt jwt =
        jwt(
            Map.of(
                "sub", "1",
                "preferred_username", "bao.teacher",
                "scope", "openid profile",
                "realm_access", Map.of("roles", List.of("instructor", "default-roles-tayjava")),
                "resource_access",
                    Map.of("tayjava-api", Map.of("roles", List.of("course-write")))));

    JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);

    assertThat(token.getName()).isEqualTo("bao.teacher");
    assertThat(token.getAuthorities().stream().map(GrantedAuthority::getAuthority))
        .contains("ROLE_INSTRUCTOR", "ROLE_COURSE_WRITE", "SCOPE_openid", "SCOPE_profile")
        .doesNotContain("ROLE_DEFAULT_ROLES_TAYJAVA");
  }

  @Test
  void ignoresTechnicalRealmRoles() {
    Jwt jwt =
        jwt(
            Map.of(
                "sub",
                "1",
                "preferred_username",
                "an.student",
                "realm_access",
                Map.of("roles", List.of("student", "offline_access", "uma_authorization"))));

    JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);

    assertThat(token.getAuthorities().stream().map(GrantedAuthority::getAuthority))
        .contains("ROLE_STUDENT")
        .doesNotContain("ROLE_OFFLINE_ACCESS", "ROLE_UMA_AUTHORIZATION");
  }

  private static Jwt jwt(Map<String, Object> claims) {
    return Jwt.withTokenValue("test")
        .header("alg", "none")
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(60))
        .claims(c -> c.putAll(claims))
        .build();
  }
}
