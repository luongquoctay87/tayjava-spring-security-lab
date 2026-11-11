package com.tayjava.config.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.tayjava.config.security.Authorities.Perm;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@SpringBootTest
class RbacUserDetailsServiceTest {

  @Autowired private UserDetailsService userDetailsService;

  @Test
  void studentLoadsRoleAndPermissionsFromDatabase() {
    UserDetails user = userDetailsService.loadUserByUsername("an.student");
    var authorities =
        user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

    assertThat(authorities).contains("ROLE_STUDENT", Perm.COURSE_READ, Perm.ENROLLMENT_READ_OWN);
    assertThat(authorities).doesNotContain(Perm.COURSE_CREATE, Perm.COURSE_DELETE);
  }

  @Test
  void adminLoadsAllPermissions() {
    UserDetails user = userDetailsService.loadUserByUsername("root.admin");
    var authorities =
        user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

    assertThat(authorities)
        .contains("ROLE_ADMIN", Perm.COURSE_DELETE, Perm.COURSE_PUBLISH, Perm.USER_MANAGE);
  }
}
