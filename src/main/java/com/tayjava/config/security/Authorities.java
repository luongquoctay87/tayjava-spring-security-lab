package com.tayjava.config.security;

/**
 * Lesson 17: single place for role names and permission strings.
 *
 * <p>{@code hasRole("ADMIN")} looks for {@code ROLE_ADMIN}. {@code hasAuthority("course:create")}
 * matches the exact permission string — no prefix is added.
 */
public final class Authorities {

  private Authorities() {}

  public static final class Role {
    public static final String STUDENT = "STUDENT";
    public static final String INSTRUCTOR = "INSTRUCTOR";
    public static final String ADMIN = "ADMIN";

    private Role() {}
  }

  public static final class Perm {
    public static final String COURSE_READ = "course:read";
    public static final String COURSE_CREATE = "course:create";
    public static final String COURSE_UPDATE_OWN = "course:update:own";
    public static final String COURSE_PUBLISH = "course:publish";
    public static final String COURSE_DELETE = "course:delete";
    public static final String ENROLLMENT_CREATE = "enrollment:create";
    public static final String ENROLLMENT_READ_OWN = "enrollment:read:own";
    public static final String USER_MANAGE = "user:manage";
    public static final String REPORT_VIEW = "report:view";

    private Perm() {}
  }
}
