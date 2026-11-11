package com.tayjava.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Lesson 07: expose Authentication / Principal from SecurityContextHolder. */
@RestController
@RequestMapping("/api/me")
public class MeController {

  @GetMapping
  public ResponseEntity<Map<String, Object>> me() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("name", auth.getName());
    body.put(
        "authorities",
        auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
    body.put("authenticated", auth.isAuthenticated());
    return ResponseEntity.ok(body);
  }
}
