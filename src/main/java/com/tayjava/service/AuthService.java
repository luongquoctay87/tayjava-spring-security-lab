package com.tayjava.service;

import com.tayjava.common.dto.auth.MfaChallengeResp;
import com.tayjava.common.dto.auth.TokenResp;
import com.tayjava.config.security.OtpAuthenticationToken;
import com.tayjava.config.security.PreAuthenticationToken;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Lesson 32: two-step login (password → optional OTP) without putting PRE_AUTH in SecurityContext. */
@Service
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final MfaTicketStore tickets;
  private final RefreshTokenService refreshTokenService;

  public AuthService(
      @Qualifier("apiAuthenticationManager") AuthenticationManager authenticationManager,
      MfaTicketStore tickets,
      RefreshTokenService refreshTokenService) {
    this.authenticationManager = authenticationManager;
    this.tickets = tickets;
    this.refreshTokenService = refreshTokenService;
  }

  public Object login(String username, String password) {
    try {
      Authentication auth =
          authenticationManager.authenticate(
              UsernamePasswordAuthenticationToken.unauthenticated(username, password));
      if (auth instanceof PreAuthenticationToken pre) {
        String mfaToken =
            tickets.issue(pre.getName(), pre.channel(), MfaTicketStore.DEFAULT_TTL);
        return MfaChallengeResp.totp(mfaToken, (int) MfaTicketStore.DEFAULT_TTL.toSeconds());
      }
      return refreshTokenService.issuePair(auth);
    } catch (AuthenticationException ex) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }
  }

  public TokenResp verifyMfa(String mfaToken, String code) {
    try {
      Authentication auth =
          authenticationManager.authenticate(
              OtpAuthenticationToken.unauthenticated(mfaToken, code));
      return refreshTokenService.issuePair(auth);
    } catch (LockedException ex) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "Too many failed attempts, sign in again");
    } catch (AuthenticationException ex) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "Invalid or expired verification code");
    }
  }
}
