package com.tayjava.demo;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

/**
 * Read Keycloak roles from {@code realm_access.roles} and
 * {@code resource_access.{clientId}.roles}, keep default {@code SCOPE_*} authorities.
 */
public final class KeycloakJwtAuthenticationConverter
    implements Converter<Jwt, AbstractAuthenticationToken> {

  private static final Set<String> IGNORED =
      Set.of("offline_access", "uma_authorization", "default-roles-tayjava");

  private final JwtGrantedAuthoritiesConverter scopeConverter = new JwtGrantedAuthoritiesConverter();
  private final String clientId;
  private final String principalClaim;

  public KeycloakJwtAuthenticationConverter(String clientId, String principalClaim) {
    this.clientId = clientId;
    this.principalClaim = principalClaim;
  }

  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    Collection<GrantedAuthority> authorities = new HashSet<>(scopeConverter.convert(jwt));
    Stream.concat(realmRoles(jwt).stream(), clientRoles(jwt).stream())
        .filter(role -> !IGNORED.contains(role))
        .map(this::toAuthority)
        .forEach(authorities::add);

    String principalName =
        jwt.hasClaim(principalClaim) ? jwt.getClaimAsString(principalClaim) : jwt.getSubject();

    return new JwtAuthenticationToken(jwt, authorities, principalName);
  }

  private List<String> realmRoles(Jwt jwt) {
    Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
    return realmAccess == null ? List.of() : rolesOf(realmAccess);
  }

  private List<String> clientRoles(Jwt jwt) {
    Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
    if (resourceAccess == null || !(resourceAccess.get(clientId) instanceof Map<?, ?> client)) {
      return List.of();
    }
    return rolesOf(client);
  }

  private List<String> rolesOf(Map<?, ?> node) {
    return node.get("roles") instanceof Collection<?> roles
        ? roles.stream().map(String::valueOf).toList()
        : List.of();
  }

  private GrantedAuthority toAuthority(String role) {
    return new SimpleGrantedAuthority(
        "ROLE_" + role.toUpperCase(Locale.ROOT).replace('-', '_'));
  }
}
