package com.tayjava.service;

import com.tayjava.common.dto.audit.AuditEntry;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.stereotype.Service;

/** Lesson 41: single write path for security audit events. */
@Service
@RequiredArgsConstructor
@Slf4j(topic = "AuditWriter")
public class AuditWriter {

  private final AuditEventRepository repository;

  public void write(AuditEntry entry) {
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("outcome", entry.outcome().name());
    if (entry.reason() != null) {
      data.put("reason", mask(entry.reason()));
    }
    if (entry.clientIp() != null) {
      data.put("clientIp", entry.clientIp());
    }
    if (entry.tenantId() != null) {
      data.put("tenantId", entry.tenantId());
    }
    repository.add(new AuditEvent(entry.at(), entry.principal(), entry.type(), data));
    log.debug("audit type={} principal={} outcome={}", entry.type(), entry.principal(), entry.outcome());
  }

  static String mask(String value) {
    if (value == null) {
      return null;
    }
    String lower = value.toLowerCase();
    if (lower.contains("password") || lower.contains("token") || lower.contains("otp") || lower.contains("secret")) {
      return "[redacted]";
    }
    return value;
  }
}
