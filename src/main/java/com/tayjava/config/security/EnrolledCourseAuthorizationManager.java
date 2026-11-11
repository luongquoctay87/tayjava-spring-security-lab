package com.tayjava.config.security;

import com.tayjava.service.EnrollmentAccessService;
import java.util.Set;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

/**
 * Lesson 19: allow lecture access when the caller is staff or enrolled in {@code {courseId}}.
 *
 * <p>Always returns an explicit {@link AuthorizationDecision} — never {@code null} (abstain).
 */
@Component
@RequiredArgsConstructor
public class EnrolledCourseAuthorizationManager
    implements AuthorizationManager<RequestAuthorizationContext> {

  private static final Set<String> STAFF = Set.of("ROLE_ADMIN", "ROLE_INSTRUCTOR");

  private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
  private final EnrollmentAccessService enrollments;

  @Override
  public AuthorizationResult authorize(
      Supplier<? extends Authentication> authentication, RequestAuthorizationContext context) {
    Long courseId = parseCourseId(context.getVariables().get("courseId"));
    if (courseId == null) {
      return new AuthorizationDecision(false);
    }

    Authentication auth = authentication.get();
    if (auth == null || !auth.isAuthenticated() || trustResolver.isAnonymous(auth)) {
      return new AuthorizationDecision(false);
    }

    boolean isStaff =
        auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(STAFF::contains);
    if (isStaff) {
      return new AuthorizationDecision(true);
    }

    return new AuthorizationDecision(enrollments.isEnrolled(auth.getName(), courseId));
  }

  private Long parseCourseId(String raw) {
    try {
      return raw == null ? null : Long.valueOf(raw);
    } catch (NumberFormatException ex) {
      return null;
    }
  }
}
