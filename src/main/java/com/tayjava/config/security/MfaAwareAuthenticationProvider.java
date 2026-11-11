package com.tayjava.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

/**
 * Lesson 32: delegate password checks to {@code DaoAuthenticationProvider}, then downgrade to
 * {@link PreAuthenticationToken} when the account requires MFA.
 */
@RequiredArgsConstructor
public class MfaAwareAuthenticationProvider implements AuthenticationProvider {

  private final AuthenticationProvider delegate;
  private final MfaPolicy mfaPolicy;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Authentication full = delegate.authenticate(authentication);
    boolean mfaRequired =
        full.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(mfaPolicy::requiresMfa);
    if (mfaRequired) {
      return PreAuthenticationToken.of(full.getName(), "TOTP");
    }
    return full;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return delegate.supports(authentication);
  }
}
