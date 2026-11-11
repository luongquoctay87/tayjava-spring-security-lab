package com.tayjava.controller;

import com.tayjava.common.dto.auth.LoginReq;
import com.tayjava.common.dto.auth.RefreshReq;
import com.tayjava.common.dto.auth.TokenResp;
import com.tayjava.service.RefreshTokenService;
import jakarta.validation.Valid;
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
 * Lesson 25: login issues access+refresh; refresh rotates with replay detection; logout revokes the
 * refresh family.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j(topic = "AuthController")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final RefreshTokenService refreshTokenService;

  @PostMapping("/login")
  public ResponseEntity<TokenResp> login(@Valid @RequestBody LoginReq request) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              UsernamePasswordAuthenticationToken.unauthenticated(
                  request.username(), request.password()));
      log.debug("Issued token pair for {}", request.username());
      return ResponseEntity.ok(refreshTokenService.issuePair(authentication));
    } catch (AuthenticationException ex) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }
  }

  @PostMapping("/refresh")
  public ResponseEntity<TokenResp> refresh(@Valid @RequestBody RefreshReq request) {
    return ResponseEntity.ok(refreshTokenService.rotate(request.refreshToken()));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@Valid @RequestBody RefreshReq request) {
    refreshTokenService.logout(request.refreshToken());
    return ResponseEntity.noContent().build();
  }
}
