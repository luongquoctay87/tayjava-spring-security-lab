package com.tayjava.config.security;

import static com.tayjava.config.security.Authorities.Perm;
import static com.tayjava.config.security.Authorities.Role;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor;

/**
 * Builds MockMvc users with the same ROLE_* + permission mix as {@code DemoUserDetailsService}.
 * Prefer this over {@code .roles(...)} once rules use {@code hasAuthority}.
 */
final class TestAuthorities {

  private TestAuthorities() {}

  static UserRequestPostProcessor student() {
    return org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
        .user("an.student")
        .authorities(
            auth("ROLE_" + Role.STUDENT),
            auth(Perm.COURSE_READ),
            auth(Perm.ENROLLMENT_CREATE),
            auth(Perm.ENROLLMENT_READ_OWN));
  }

  static UserRequestPostProcessor instructor() {
    return org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
        .user("bao.teacher")
        .authorities(
            auth("ROLE_" + Role.INSTRUCTOR),
            auth(Perm.COURSE_READ),
            auth(Perm.COURSE_CREATE),
            auth(Perm.COURSE_UPDATE_OWN),
            auth(Perm.ENROLLMENT_READ_OWN),
            auth(Perm.REPORT_VIEW));
  }

  static UserRequestPostProcessor admin() {
    return org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
        .user("root.admin")
        .authorities(
            auth("ROLE_" + Role.ADMIN),
            auth(Perm.COURSE_READ),
            auth(Perm.COURSE_CREATE),
            auth(Perm.COURSE_UPDATE_OWN),
            auth(Perm.COURSE_PUBLISH),
            auth(Perm.COURSE_DELETE),
            auth(Perm.ENROLLMENT_CREATE),
            auth(Perm.ENROLLMENT_READ_OWN),
            auth(Perm.USER_MANAGE),
            auth(Perm.REPORT_VIEW));
  }

  private static SimpleGrantedAuthority auth(String value) {
    return new SimpleGrantedAuthority(value);
  }
}
