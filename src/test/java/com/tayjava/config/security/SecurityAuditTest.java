package com.tayjava.config.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuditTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private AuditEventRepository auditEventRepository;

  @Test
  void successfulLoginWritesAuditWithoutSecrets() throws Exception {
    Instant before = Instant.now().minusSeconds(1);
    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"an.student\",\"password\":\"demo1234\"}"))
        .andExpect(status().isOk());

    List<AuditEvent> events = auditEventRepository.find("an.student", before, "AUTH_LOGIN");
    assertThat(events).isNotEmpty();
    String blob = events.toString();
    assertThat(blob).doesNotContain("demo1234");
    assertThat(blob.toLowerCase()).doesNotContain("password=");
  }

  @Test
  void unauthorizedApiStill401AndDoesNotLeakToken() throws Exception {
    Instant before = Instant.now().minusSeconds(1);
    mockMvc
        .perform(get("/api/enrollments/me").header("Authorization", "Bearer secret-token-value"))
        .andExpect(status().isUnauthorized());
    List<AuditEvent> recent = auditEventRepository.find(null, before, null);
    String blob = recent.toString();
    assertThat(blob).doesNotContain("secret-token-value");
    assertThat(blob).contains("redacted-principal");
  }
}
