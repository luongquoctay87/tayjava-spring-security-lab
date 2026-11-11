package com.tayjava.config.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

/**
 * Lesson 24: map JWT claims {@code roles} → {@code ROLE_*} and {@code permissions} → fine-grained
 * authorities so {@code hasAuthority("course:create")} keeps working after Bearer auth.
 */
@Configuration
public class JwtAuthoritiesConfig {

  @Bean
  JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
    roles.setAuthoritiesClaimName("roles");
    roles.setAuthorityPrefix("ROLE_");

    Converter<Jwt, Collection<GrantedAuthority>> combined =
        jwt -> {
          Collection<GrantedAuthority> authorities = new ArrayList<>(roles.convert(jwt));
          Object claim = jwt.getClaim("permissions");
          if (claim instanceof Collection<?> list) {
            for (Object permission : list) {
              if (permission != null && !permission.toString().isBlank()) {
                authorities.add(new SimpleGrantedAuthority(permission.toString()));
              }
            }
          }
          return List.copyOf(authorities);
        };

    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(combined);
    converter.setPrincipalClaimName("sub");
    return converter;
  }
}
