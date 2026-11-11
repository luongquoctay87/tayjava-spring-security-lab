package com.tayjava.service;

import com.tayjava.config.security.Authorities.Perm;
import com.tayjava.config.security.Authorities.Role;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Lesson 17/18: emit both {@code ROLE_*} and fine permissions; {@code chi.teacher} supports
 * ownership demos in method security.
 */
@Service
public class DemoUserDetailsService implements UserDetailsService {

  private final Map<String, UserDetails> users = new ConcurrentHashMap<>();

  public DemoUserDetailsService(PasswordEncoder passwordEncoder) {
    String hash = passwordEncoder.encode("demo1234");
    users.put(
        "an.student",
        user(
            "an.student",
            hash,
            Role.STUDENT,
            List.of(Perm.COURSE_READ, Perm.ENROLLMENT_CREATE, Perm.ENROLLMENT_READ_OWN)));
    users.put(
        "bao.teacher",
        user(
            "bao.teacher",
            hash,
            Role.INSTRUCTOR,
            instructorPerms()));
    users.put(
        "chi.teacher",
        user(
            "chi.teacher",
            hash,
            Role.INSTRUCTOR,
            instructorPerms()));
    users.put(
        "root.admin",
        user(
            "root.admin",
            hash,
            Role.ADMIN,
            List.of(
                Perm.COURSE_READ,
                Perm.COURSE_CREATE,
                Perm.COURSE_UPDATE_OWN,
                Perm.COURSE_PUBLISH,
                Perm.COURSE_DELETE,
                Perm.ENROLLMENT_CREATE,
                Perm.ENROLLMENT_READ_OWN,
                Perm.USER_MANAGE,
                Perm.REPORT_VIEW)));
  }

  private static List<String> instructorPerms() {
    return List.of(
        Perm.COURSE_READ,
        Perm.COURSE_CREATE,
        Perm.COURSE_UPDATE_OWN,
        Perm.ENROLLMENT_READ_OWN,
        Perm.REPORT_VIEW);
  }

  private static UserDetails user(
      String username, String passwordHash, String role, List<String> permissions) {
    var authorities =
        Stream.concat(
                Stream.of(new SimpleGrantedAuthority("ROLE_" + role)),
                permissions.stream().map(SimpleGrantedAuthority::new))
            .toList();
    return User.withUsername(username).password(passwordHash).authorities(authorities).build();
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
