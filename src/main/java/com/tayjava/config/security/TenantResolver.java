package com.tayjava.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/** Lesson 34: tenant comes from a verified JWT claim — never from a client header alone. */
public final class TenantResolver {

  public static final String TENANT_CLAIM = "tenant_id";

  private TenantResolver() {}

  public static String fromAuthentication(Authentication authentication) {
    if (authentication instanceof JwtAuthenticationToken jwtAuth) {
      Jwt jwt = jwtAuth.getToken();
      String tenant = jwt.getClaimAsString(TENANT_CLAIM);
      if (tenant == null || tenant.isBlank()) {
        throw new IllegalStateException("Token missing claim " + TENANT_CLAIM);
      }
      return tenant;
    }
    return null;
  }
}
