package com.tayjava.controller;

import com.tayjava.common.dto.auth.LoginReq;
import com.tayjava.common.dto.auth.MfaVerifyReq;
import com.tayjava.common.dto.auth.RefreshReq;
import com.tayjava.common.dto.auth.TokenResp;
import com.tayjava.service.AuthService;
import com.tayjava.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 32: login may return tokens or an MFA challenge; {@code /mfa/verify} completes OTP.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j(topic = "AuthController")
public class AuthController {

  private final AuthService authService;
  private final RefreshTokenService refreshTokenService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginReq request) {
    Object outcome = authService.login(request.username(), request.password());
    log.debug("Login outcome for {}: {}", request.username(), outcome.getClass().getSimpleName());
    return ResponseEntity.ok(outcome);
  }

  @PostMapping("/mfa/verify")
  public ResponseEntity<TokenResp> verifyMfa(@Valid @RequestBody MfaVerifyReq request) {
    return ResponseEntity.ok(authService.verifyMfa(request.mfaToken(), request.code()));
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
