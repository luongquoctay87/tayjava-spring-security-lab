package com.tayjava.config.security;

import java.util.Collection;
import java.util.Collections;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** Lesson 32: custom token type for OTP / MFA verification. */
public final class OtpAuthenticationToken extends AbstractAuthenticationToken {

  private final Object principal;
  private final String mfaToken;
  private final String code;

  private OtpAuthenticationToken(String mfaToken, String code) {
    super(Collections.emptyList());
    this.principal = null;
    this.mfaToken = mfaToken;
    this.code = code;
    setAuthenticated(false);
  }

  private OtpAuthenticationToken(
      UserDetails principal, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.principal = principal;
    this.mfaToken = null;
    this.code = null;
    setAuthenticated(true);
  }

  public static OtpAuthenticationToken unauthenticated(String mfaToken, String code) {
    return new OtpAuthenticationToken(mfaToken, code);
  }

  public static OtpAuthenticationToken authenticated(UserDetails user) {
    return new OtpAuthenticationToken(user, user.getAuthorities());
  }

  public String mfaToken() {
    return mfaToken;
  }

  public String code() {
    return code;
  }

  @Override
  public Object getCredentials() {
    return code;
  }

  @Override
  public Object getPrincipal() {
    return principal;
  }
}
