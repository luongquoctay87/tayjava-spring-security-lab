package com.tayjava.config.security;

import com.tayjava.service.CourseService;
import java.io.Serializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Lesson 35: central hasPermission(...) for domain objects (Course WRITE/READ).
 * Unknown types fail closed.
 */
@Component
@RequiredArgsConstructor
public class TayJavaPermissionEvaluator implements PermissionEvaluator {

  private final CourseService courseService;
  private final CourseSecurity courseSecurity;

  @Override
  public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
    return false; // prefer id + type form for lab endpoints
  }

  @Override
  public boolean hasPermission(
      Authentication auth, Serializable targetId, String targetType, Object permission) {
    if (auth == null || targetId == null || permission == null || targetType == null) {
      return false;
    }
    if (!"Course".equalsIgnoreCase(targetType)) {
      return false;
    }
    Long courseId = targetId instanceof Long id ? id : Long.valueOf(targetId.toString());
    String perm = permission.toString();
    if ("WRITE".equalsIgnoreCase(perm) || "UPDATE".equalsIgnoreCase(perm)) {
      return auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))
          || courseSecurity.isOwner(courseId, auth);
    }
    if ("READ".equalsIgnoreCase(perm)) {
      return courseService.findInstructorUsername(courseId).isPresent()
          || auth.isAuthenticated();
    }
    return false;
  }
}
