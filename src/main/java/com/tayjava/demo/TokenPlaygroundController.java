package com.tayjava.demo;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Learning-only token mint endpoint. Do not keep on the Auth API project line.
 */
@RestController
@RequiredArgsConstructor
public class TokenPlaygroundController {

  private final JwtDemo jwtDemo;

  public record DebugToken(String token, long expiresIn) {}

  @GetMapping("/api/debug/token")
  public DebugToken debugToken() {
    var issued = jwtDemo.issueForDemo("42", List.of("INSTRUCTOR"));
    return new DebugToken(issued.value(), issued.expiresIn());
  }
}
