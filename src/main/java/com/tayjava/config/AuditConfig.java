package com.tayjava.config;

import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Lesson 41: lab uses in-memory audit repo; production would use append-only JDBC. */
@Configuration
public class AuditConfig {

  @Bean
  AuditEventRepository auditEventRepository() {
    return new InMemoryAuditEventRepository();
  }
}
