package com.tayjava.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Lesson 16: authenticated enrollment surface used by the authorizeHttpRequests matrix. */
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

  @GetMapping("/me")
  public ResponseEntity<Map<String, Object>> me(Authentication authentication) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("username", authentication.getName());
    body.put("items", List.of());
    return ResponseEntity.ok(body);
  }
}
