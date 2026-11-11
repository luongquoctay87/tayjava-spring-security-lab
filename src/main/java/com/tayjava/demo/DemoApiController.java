package com.tayjava.demo;

import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoApiController {

  @GetMapping("/public")
  public Map<String, String> open() {
    return Map.of("message", "anyone can call");
  }

  @GetMapping("/me")
  public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
    return Map.of(
        "sub", jwt.getSubject(),
        "preferred_username", jwt.getClaimAsString("preferred_username"),
        "realm_access", jwt.getClaimAsMap("realm_access"));
  }

  @GetMapping("/admin/ping")
  public Map<String, String> adminPing() {
    return Map.of("message", "admin ok");
  }
}
