package com.tayjava.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 16: public auth stubs so {@code /api/auth/**} matchers are reachable.
 * JWT login arrives in a later lesson.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login() {
    return ResponseEntity.ok(Map.of("status", "login-stub"));
  }

  @PostMapping("/refresh")
  public ResponseEntity<Map<String, String>> refresh() {
    return ResponseEntity.ok(Map.of("status", "refresh-stub"));
  }
}
