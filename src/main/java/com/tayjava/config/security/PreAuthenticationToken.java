package com.tayjava.config.security;

import java.util.Collections;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * Lesson 32: password step succeeded but MFA is still required. Never place this in
 * {@code SecurityContext} — AuthService issues an opaque mfa ticket instead.
 */
public final class PreAuthenticationToken extends AbstractAuthenticationToken {

  private final String username;
  private final String channel;

  private PreAuthenticationToken(String username, String channel) {
    super(Collections.emptyList());
    this.username = username;
    this.channel = channel;
    setAuthenticated(false);
  }

  public static PreAuthenticationToken of(String username, String channel) {
    return new PreAuthenticationToken(username, channel);
  }

  public String channel() {
    return channel;
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return username;
  }

  @Override
  public String getName() {
    return username;
  }
}
