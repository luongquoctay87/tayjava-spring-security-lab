package com.tayjava.service;

import com.tayjava.config.security.Authorities;
import com.tayjava.entity.AppUser;
import com.tayjava.entity.Role;
import com.tayjava.repository.AppUserRepository;
import com.tayjava.repository.RoleRepository;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Lesson 28: link an OAuth2/OIDC identity to a local {@link AppUser}. Existing email matches are
 * reused; first-time social sign-in creates a STUDENT account with an unusable random password.
 */
@Service
@RequiredArgsConstructor
@Slf4j(topic = "OAuthAppUserService")
public class OAuthAppUserService {

  private final AppUserRepository appUsers;
  private final RoleRepository roles;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public AppUser linkOrCreate(String email, String preferredUsername, String registrationId) {
    if (!StringUtils.hasText(email)) {
      throw new IllegalArgumentException(
          "OAuth provider " + registrationId + " did not return an email");
    }
    String normalized = email.trim().toLowerCase(Locale.ROOT);
    return appUsers
        .findByEmailIgnoreCase(normalized)
        .orElseGet(() -> createStudent(normalized, preferredUsername, registrationId));
  }

  private AppUser createStudent(String email, String preferredUsername, String registrationId) {
    Role student =
        roles
            .findByName(Authorities.Role.STUDENT)
            .orElseThrow(() -> new IllegalStateException("STUDENT role missing — seed RBAC first"));
    String base =
        StringUtils.hasText(preferredUsername)
            ? preferredUsername.replaceAll("[^a-zA-Z0-9._-]", ".")
            : email.substring(0, email.indexOf('@'));
    String username = uniqueUsername(base);
    AppUser user =
        new AppUser(username, passwordEncoder.encode("oauth-only-" + UUID.randomUUID()), email);
    user.setRoles(Set.of(student));
    AppUser saved = appUsers.save(user);
    log.info("Created AppUser {} from OAuth provider {}", saved.getUsername(), registrationId);
    return saved;
  }

  private String uniqueUsername(String base) {
    String candidate = base.toLowerCase(Locale.ROOT);
    if (!appUsers.existsByUsername(candidate)) {
      return candidate;
    }
    for (int i = 1; i < 1000; i++) {
      String next = candidate + "." + i;
      if (!appUsers.existsByUsername(next)) {
        return next;
      }
    }
    return candidate + "." + UUID.randomUUID().toString().substring(0, 8);
  }
}
