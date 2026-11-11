package com.tayjava.service;

import com.tayjava.common.dto.course.CourseResp;
import com.tayjava.config.security.Authorities.Perm;
import com.tayjava.config.security.TenantContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Lesson 34: courses carry tenant_id; mutating operations require matching TenantContext.
 */
@Service
public class CourseServiceImpl implements CourseService {

  private final AtomicLong seq = new AtomicLong(3);
  private final Map<Long, CourseRecord> courses = new ConcurrentHashMap<>();

  public CourseServiceImpl() {
    courses.put(1L, new CourseRecord(1L, "Spring Security Fundamentals", "spring-security-fundamentals", "bao.teacher", "acme"));
    courses.put(2L, new CourseRecord(2L, "Java Core from A to Z", "java-core-from-a-to-z", "chi.teacher", "acme"));
    courses.put(3L, new CourseRecord(3L, "Globex Internal Onboarding", "globex-internal-onboarding", "gina.teacher", "globex"));
  }

  @Override
  public List<CourseResp> list() {
    return courses.values().stream().map(this::toResp).toList();
  }

  @Override
  public CourseResp get(Long id) {
    return toResp(require(id));
  }

  @Override
  @PreAuthorize("hasAuthority('" + Perm.COURSE_CREATE + "')")
  public CourseResp create(String title, String instructorUsername) {
    String tenant = resolveTenantForCreate();
    long id = seq.incrementAndGet();
    String slug = title.toLowerCase().replace(' ', '-');
    CourseRecord created = new CourseRecord(id, title, slug, instructorUsername, tenant);
    courses.put(id, created);
    return toResp(created);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN') or @courseSecurity.isOwner(#courseId, authentication)")
  public CourseResp update(Long courseId, String title) {
    CourseRecord existing = requireSameTenant(courseId);
    CourseRecord updated =
        new CourseRecord(
            existing.id(), title, existing.slug(), existing.instructorUsername(), existing.tenantId());
    courses.put(courseId, updated);
    return toResp(updated);
  }

  @Override
  @PreAuthorize("hasAuthority('" + Perm.COURSE_DELETE + "')")
  public void delete(Long courseId) {
    requireSameTenant(courseId);
    if (courses.remove(courseId) == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found: " + courseId);
    }
  }

  @Override
  public Optional<String> findInstructorUsername(Long courseId) {
    return Optional.ofNullable(courses.get(courseId)).map(CourseRecord::instructorUsername);
  }

  public Optional<String> findTenantId(Long courseId) {
    return Optional.ofNullable(courses.get(courseId)).map(CourseRecord::tenantId);
  }

  private CourseRecord require(Long id) {
    CourseRecord course = courses.get(id);
    if (course == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found: " + id);
    }
    return course;
  }

  private CourseRecord requireSameTenant(Long id) {
    CourseRecord course = require(id);
    String tenant = TenantContext.get();
    // JWT path sets TenantContext; @WithMockUser tests leave NONE and skip isolation.
    if (!TenantContext.NONE.equals(tenant) && !tenant.equals(course.tenantId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cross-tenant access denied");
    }
    return course;
  }

  private static String resolveTenantForCreate() {
    String tenant = TenantContext.get();
    return TenantContext.NONE.equals(tenant) ? "acme" : tenant;
  }

  private CourseResp toResp(CourseRecord course) {
    return new CourseResp(course.id(), course.title(), course.slug());
  }

  private record CourseRecord(
      Long id, String title, String slug, String instructorUsername, String tenantId) {}
}
