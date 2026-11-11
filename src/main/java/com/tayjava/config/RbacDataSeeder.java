package com.tayjava.config;

import com.tayjava.config.security.Authorities.Perm;
import com.tayjava.config.security.Authorities.Role;
import com.tayjava.entity.AppUser;
import com.tayjava.entity.Permission;
import com.tayjava.repository.AppUserRepository;
import com.tayjava.repository.PermissionRepository;
import com.tayjava.repository.RoleRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lesson 20: seed permissions, roles, and demo users when the database is empty.
 *
 * <p>Lab password {@code demo1234} is hashed at runtime — never commit a production secret.
 */
@Component
@RequiredArgsConstructor
@Slf4j(topic = "RbacDataSeeder")
public class RbacDataSeeder implements ApplicationRunner {

  private final PermissionRepository permissionRepository;
  private final RoleRepository roleRepository;
  private final AppUserRepository appUserRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (appUserRepository.count() > 0) {
      return;
    }
    log.info("Seeding RBAC demo data");

    Map<String, Permission> permissions = seedPermissions();
    Map<String, com.tayjava.entity.Role> roles = seedRoles(permissions);
    String hash = passwordEncoder.encode("demo1234");

    seedUser("an.student", hash, "an.student@tayjava.com", "acme", roles.get(Role.STUDENT));
    seedUser("bao.teacher", hash, "bao.teacher@tayjava.com", "acme", roles.get(Role.INSTRUCTOR));
    seedUser("chi.teacher", hash, "chi.teacher@tayjava.com", "acme", roles.get(Role.INSTRUCTOR));
    seedUser("root.admin", hash, "root.admin@tayjava.com", "acme", roles.get(Role.ADMIN));
    seedUser("gina.teacher", hash, "gina.teacher@globex.com", "globex", roles.get(Role.INSTRUCTOR));
  }

  private Map<String, Permission> seedPermissions() {
    Map<String, Permission> map = new LinkedHashMap<>();
    for (String name :
        List.of(
            Perm.COURSE_READ,
            Perm.COURSE_CREATE,
            Perm.COURSE_UPDATE_OWN,
            Perm.COURSE_PUBLISH,
            Perm.COURSE_DELETE,
            Perm.ENROLLMENT_CREATE,
            Perm.ENROLLMENT_READ_OWN,
            Perm.USER_MANAGE,
            Perm.REPORT_VIEW)) {
      Permission permission = permissionRepository.save(new Permission(name));
      map.put(name, permission);
    }
    return map;
  }

  private Map<String, com.tayjava.entity.Role> seedRoles(Map<String, Permission> permissions) {
    Map<String, com.tayjava.entity.Role> roles = new LinkedHashMap<>();

    com.tayjava.entity.Role student = new com.tayjava.entity.Role(Role.STUDENT);
    student.setPermissions(
        Set.of(
            permissions.get(Perm.COURSE_READ),
            permissions.get(Perm.ENROLLMENT_CREATE),
            permissions.get(Perm.ENROLLMENT_READ_OWN)));
    roles.put(Role.STUDENT, roleRepository.save(student));

    com.tayjava.entity.Role instructor = new com.tayjava.entity.Role(Role.INSTRUCTOR);
    instructor.setPermissions(
        Set.of(
            permissions.get(Perm.COURSE_READ),
            permissions.get(Perm.COURSE_CREATE),
            permissions.get(Perm.COURSE_UPDATE_OWN),
            permissions.get(Perm.ENROLLMENT_READ_OWN),
            permissions.get(Perm.REPORT_VIEW)));
    roles.put(Role.INSTRUCTOR, roleRepository.save(instructor));

    com.tayjava.entity.Role admin = new com.tayjava.entity.Role(Role.ADMIN);
    admin.setPermissions(Set.copyOf(permissions.values()));
    roles.put(Role.ADMIN, roleRepository.save(admin));

    return roles;
  }

  private void seedUser(
      String username,
      String passwordHash,
      String email,
      String tenantId,
      com.tayjava.entity.Role role) {
    AppUser user = new AppUser(username, passwordHash, email, tenantId);
    user.setRoles(Set.of(role));
    appUserRepository.save(user);
  }
}
