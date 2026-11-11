package com.tayjava.controller;

import com.tayjava.common.dto.auth.LoginReq;
import com.tayjava.common.dto.auth.TokenResp;
import com.tayjava.service.AccessTokenService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Lesson 23: {@code POST /api/auth/login} authenticates via {@link AuthenticationManager} and
 * returns a signed JWT. Refresh remains a stub until the refresh-token lesson.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j(topic = "AuthController")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final AccessTokenService accessTokenService;

  @PostMapping("/login")
  public ResponseEntity<TokenResp> login(@Valid @RequestBody LoginReq request) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              UsernamePasswordAuthenticationToken.unauthenticated(
                  request.username(), request.password()));
      log.debug("Issued access token for {}", request.username());
      return ResponseEntity.ok(accessTokenService.issue(authentication));
    } catch (AuthenticationException ex) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }
  }

  @PostMapping("/refresh")
  public ResponseEntity<Map<String, String>> refresh() {
    return ResponseEntity.ok(Map.of("status", "refresh-stub"));
  }
}
