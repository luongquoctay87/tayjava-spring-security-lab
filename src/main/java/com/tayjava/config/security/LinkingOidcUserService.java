package com.tayjava.config.security;

import com.tayjava.entity.AppUser;
import com.tayjava.service.OAuthAppUserService;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

/** Lesson 28: OIDC (e.g. Google) user info → local AppUser authorities. */
@Service
@RequiredArgsConstructor
public class LinkingOidcUserService extends OidcUserService {

  private final OAuthAppUserService oAuthAppUserService;

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    OidcUser oidcUser = super.loadUser(userRequest);
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    AppUser appUser =
        oAuthAppUserService.linkOrCreate(
            oidcUser.getEmail(), oidcUser.getPreferredUsername(), registrationId);

    Set<GrantedAuthority> authorities = new HashSet<>();
    appUser.getRoles().forEach(role -> {
      authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
      role.getPermissions()
          .forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getName())));
    });

    return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
  }
}
