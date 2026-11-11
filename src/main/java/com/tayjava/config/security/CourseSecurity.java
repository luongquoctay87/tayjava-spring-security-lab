package com.tayjava.config.security;

import com.tayjava.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/** SpEL helper for {@code @PreAuthorize} ownership checks (lesson 18). */
@Component("courseSecurity")
@RequiredArgsConstructor
public class CourseSecurity {

  private final CourseService courseService;

  public boolean isOwner(Long courseId, Authentication authentication) {
    if (authentication == null || courseId == null) {
      return false;
    }
    return courseService
        .findInstructorUsername(courseId)
        .map(owner -> owner.equalsIgnoreCase(authentication.getName()))
        .orElse(false);
  }
}
