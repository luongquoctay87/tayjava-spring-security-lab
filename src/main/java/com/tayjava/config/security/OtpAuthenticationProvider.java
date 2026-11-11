package com.tayjava.config.security;

import com.tayjava.service.LabOtpService;
import com.tayjava.service.MfaTicketStore;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Lesson 32: validates opaque MFA ticket + OTP code, then returns a fully authenticated principal.
 */
@RequiredArgsConstructor
public class OtpAuthenticationProvider implements AuthenticationProvider {

  private final MfaTicketStore tickets;
  private final LabOtpService labOtpService;
  private final UserDetailsService userDetailsService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    OtpAuthenticationToken token = (OtpAuthenticationToken) authentication;
    MfaTicketStore.MfaTicket ticket =
        tickets
            .peek(token.mfaToken())
            .orElseThrow(() -> new BadCredentialsException("Invalid or expired MFA ticket"));

    if (ticket.attempts() >= MfaTicketStore.MAX_ATTEMPTS) {
      tickets.invalidate(token.mfaToken());
      throw new LockedException("Too many failed MFA attempts");
    }

    if (!labOtpService.matches(token.code())) {
      tickets.recordFailure(token.mfaToken());
      throw new BadCredentialsException("Invalid MFA code");
    }

    tickets.consume(token.mfaToken());
    UserDetails user = userDetailsService.loadUserByUsername(ticket.username());
    return OtpAuthenticationToken.authenticated(user);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return OtpAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
