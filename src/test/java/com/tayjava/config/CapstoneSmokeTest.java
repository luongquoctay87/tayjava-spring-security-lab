package com.tayjava.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tayjava.config.security.AuthLoginSupport;
import com.tayjava.config.security.TestAuthorities;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CapstoneSmokeTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private Flyway flyway;

  @Test
  void flywayMigrationsApplied() {
    assertThat(flyway.info().applied()).hasSizeGreaterThanOrEqualTo(3);
  }

  @Test
  void neoEndpointsHappyAndUnauthorized() throws Exception {
    mockMvc.perform(get("/api/courses")).andExpect(status().isOk());

    mockMvc.perform(get("/api/enrollments/me")).andExpect(status().isUnauthorized());

    mockMvc
        .perform(
            post("/api/courses")
                .with(TestAuthorities.instructor())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Capstone Course\"}"))
        .andExpect(status().isCreated());

    String token = AuthLoginSupport.accessToken(mockMvc, "an.student", "demo1234");
    mockMvc
        .perform(get("/api/enrollments/me").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("an.student"));
  }
}
