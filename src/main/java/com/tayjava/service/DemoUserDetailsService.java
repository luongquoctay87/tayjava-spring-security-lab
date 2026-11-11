package com.tayjava.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Lesson 08: explicit {@link UserDetailsService} used by AuthenticationManager.
 * Still in-memory — RBAC tables arrive later.
 */
@Service
public class DemoUserDetailsService implements UserDetailsService {

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return switch (username) {
      case "an.student" -> User.withUsername("an.student").password("{noop}demo1234").roles("STUDENT").build();
      case "bao.teacher" ->
          User.withUsername("bao.teacher").password("{noop}demo1234").roles("INSTRUCTOR").build();
      case "root.admin" -> User.withUsername("root.admin").password("{noop}demo1234").roles("ADMIN").build();
      default -> throw new UsernameNotFoundException("Unknown user: " + username);
    };
  }
}
