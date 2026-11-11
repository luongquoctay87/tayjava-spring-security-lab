package com.tayjava.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/** Lesson 19: in-memory enrollment lookup for custom AuthorizationManager demos. */
@Service
public class EnrollmentAccessService {

  private final Map<String, Set<Long>> enrollments = new ConcurrentHashMap<>();

  public EnrollmentAccessService() {
    enrollments.put("an.student", Set.of(1L));
  }

  public boolean isEnrolled(String username, Long courseId) {
    if (username == null || courseId == null) {
      return false;
    }
    return enrollments.getOrDefault(username, Set.of()).contains(courseId);
  }
}
