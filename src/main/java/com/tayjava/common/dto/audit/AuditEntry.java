package com.tayjava.common.dto.audit;

import com.tayjava.common.enums.AuditOutcome;
import java.time.Instant;

/** Lesson 41: structured immutable audit record (no secrets). */
public record AuditEntry(
    Instant at,
    String type,
    String principal,
    AuditOutcome outcome,
    String reason,
    String clientIp,
    String tenantId) {

  public static AuditEntry of(
      String type,
      String principal,
      AuditOutcome outcome,
      String reason,
      String clientIp,
      String tenantId) {
    return new AuditEntry(Instant.now(), type, principal, outcome, reason, clientIp, tenantId);
  }
}
