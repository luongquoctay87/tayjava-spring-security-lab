package com.tayjava.config.security;

import com.tayjava.entity.AppUser;
import com.tayjava.service.OAuthAppUserService;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/** Lesson 28: OAuth2 (e.g. GitHub) user info → local AppUser authorities. */
@Service
@RequiredArgsConstructor
public class LinkingOAuth2UserService extends DefaultOAuth2UserService {

  private final OAuthAppUserService oAuthAppUserService;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oauthUser = super.loadUser(userRequest);
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    String email = extractEmail(oauthUser.getAttributes());
    String login = stringAttr(oauthUser.getAttributes(), "login");
    AppUser appUser = oAuthAppUserService.linkOrCreate(email, login, registrationId);

    Set<GrantedAuthority> authorities = new HashSet<>();
    appUser.getRoles().forEach(role -> {
      authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
      role.getPermissions()
          .forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getName())));
    });

    Map<String, Object> attributes = oauthUser.getAttributes();
    String nameAttribute =
        userRequest
            .getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUserNameAttributeName();
    return new DefaultOAuth2User(authorities, attributes, nameAttribute);
  }

  private static String extractEmail(Map<String, Object> attributes) {
    String email = stringAttr(attributes, "email");
    if (email != null) {
      return email;
    }
    // GitHub may omit email when scope/user privacy blocks it — fall back to noreply form.
    String login = stringAttr(attributes, "login");
    if (login != null) {
      return login + "@users.noreply.github.com";
    }
    return null;
  }

  private static String stringAttr(Map<String, Object> attributes, String key) {
    Object value = attributes.get(key);
    return value == null ? null : value.toString();
  }
}
