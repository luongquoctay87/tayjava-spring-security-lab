package com.tayjava.config.security;

import org.springframework.stereotype.Component;

/** Lesson 32: MFA required for elevated roles (INSTRUCTOR / ADMIN). */
@Component
public class MfaPolicy {

  public boolean requiresMfa(String authority) {
    return "ROLE_INSTRUCTOR".equals(authority) || "ROLE_ADMIN".equals(authority);
  }

  public boolean requiresMfaForUsernameAuthorities(
      java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> authorities) {
    return authorities.stream()
        .map(org.springframework.security.core.GrantedAuthority::getAuthority)
        .anyMatch(this::requiresMfa);
  }
}
