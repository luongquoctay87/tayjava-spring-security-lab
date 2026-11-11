package com.tayjava.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Lesson 09: passwords stored with {@link PasswordEncoder} (BCrypt), not {noop}.
 */
@Service
public class DemoUserDetailsService implements UserDetailsService {

  private final Map<String, UserDetails> users = new ConcurrentHashMap<>();

  public DemoUserDetailsService(PasswordEncoder passwordEncoder) {
    users.put(
        "an.student",
        User.withUsername("an.student")
            .password(passwordEncoder.encode("demo1234"))
            .roles("STUDENT")
            .build());
    users.put(
        "bao.teacher",
        User.withUsername("bao.teacher")
            .password(passwordEncoder.encode("demo1234"))
            .roles("INSTRUCTOR")
            .build());
    users.put(
        "root.admin",
        User.withUsername("root.admin")
            .password(passwordEncoder.encode("demo1234"))
            .roles("ADMIN")
            .build());
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserDetails user = users.get(username);
    if (user == null) {
      throw new UsernameNotFoundException("Unknown user: " + username);
    }
    return user;
  }
}
