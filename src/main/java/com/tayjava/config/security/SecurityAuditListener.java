package com.tayjava.config.security;

import com.tayjava.common.dto.audit.AuditEntry;
import com.tayjava.common.enums.AuditOutcome;
import com.tayjava.config.AuditConfig;
import com.tayjava.service.AuditWriter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Lesson 41: map Spring Security events into structured AuditEntry records. */
@Component
@RequiredArgsConstructor
public class SecurityAuditListener {

  private final AuditWriter writer;

  @EventListener
  public void onSuccess(AuthenticationSuccessEvent event) {
    writer.write(
        AuditEntry.of(
            "AUTH_LOGIN",
            AuditConfig.sanitizePrincipal(event.getAuthentication().getName()),
            AuditOutcome.SUCCESS,
            null,
            clientIp(),
            TenantContext.get()));
  }

  @EventListener
  public void onFailure(AbstractAuthenticationFailureEvent event) {
    String principal =
        event.getAuthentication() != null
            ? AuditConfig.sanitizePrincipal(event.getAuthentication().getName())
            : "anonymous";
    writer.write(
        AuditEntry.of(
            "AUTH_LOGIN",
            principal,
            AuditOutcome.FAILURE,
            event.getException() != null ? event.getException().getClass().getSimpleName() : "AUTH_FAILED",
            clientIp(),
            TenantContext.get()));
  }

  @EventListener
  public void onDenied(AuthorizationDeniedEvent<?> event) {
    String principal =
        event.getAuthentication().get() != null
            ? AuditConfig.sanitizePrincipal(event.getAuthentication().get().getName())
            : "anonymous";
    writer.write(
        AuditEntry.of(
            "ACCESS_DENIED",
            principal,
            AuditOutcome.DENIED,
            "AUTHORIZATION_DENIED",
            clientIp(),
            TenantContext.get()));
  }

  private static String clientIp() {
    ServletRequestAttributes attrs =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attrs == null) {
      return null;
    }
    return attrs.getRequest().getRemoteAddr();
  }
}
