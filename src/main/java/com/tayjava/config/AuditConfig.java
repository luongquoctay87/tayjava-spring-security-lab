package com.tayjava.config;

import java.time.Instant;
import java.util.List;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Lesson 41: lab in-memory audit repo with principal masking for bearer failures. */
@Configuration
public class AuditConfig {

  @Bean
  AuditEventRepository auditEventRepository() {
    InMemoryAuditEventRepository delegate = new InMemoryAuditEventRepository();
    return new AuditEventRepository() {
      @Override
      public void add(AuditEvent event) {
        delegate.add(
            new AuditEvent(
                event.getTimestamp(),
                sanitizePrincipal(event.getPrincipal()),
                event.getType(),
                event.getData()));
      }

      @Override
      public List<AuditEvent> find(String principal, Instant after, String type) {
        return delegate.find(sanitizePrincipal(principal), after, type);
      }
    };
  }

  public static String sanitizePrincipal(String principal) {
    if (principal == null) {
      return null;
    }
    String lower = principal.toLowerCase();
    boolean looksLikeJwt = principal.chars().filter(ch -> ch == '.').count() >= 2;
    if (looksLikeJwt || principal.length() > 64 || lower.contains("token") || lower.contains("bearer")) {
      return "redacted-principal";
    }
    return principal;
  }
}
