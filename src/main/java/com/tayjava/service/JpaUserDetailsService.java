package com.tayjava.service;

import com.tayjava.entity.AppUser;
import com.tayjava.entity.Permission;
import com.tayjava.repository.AppUserRepository;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Lesson 37: soft-lock via LoginAttemptService → isAccountNonLocked(). */
@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

  private final AppUserRepository appUserRepository;
  private final LoginAttemptService loginAttemptService;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AppUser user =
        appUserRepository
            .findByUsernameWithRolesAndPermissions(username)
            .orElseThrow(() -> new UsernameNotFoundException("Unknown user: " + username));

    boolean locked = loginAttemptService.isUsernameLocked(user.getUsername());

    return User.withUsername(user.getUsername())
        .password(user.getPassword())
        .authorities(toAuthorities(user))
        .disabled(!user.isEnabled())
        .accountLocked(locked)
        .build();
  }

  private List<GrantedAuthority> toAuthorities(AppUser user) {
    return user.getRoles().stream()
        .flatMap(
            role ->
                Stream.concat(
                    Stream.of("ROLE_" + role.getName()),
                    role.getPermissions().stream().map(Permission::getName)))
        .distinct()
        .<GrantedAuthority>map(SimpleGrantedAuthority::new)
        .toList();
  }
}
