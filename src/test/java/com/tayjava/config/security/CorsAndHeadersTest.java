package com.tayjava.config.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CorsAndHeadersTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void preflightFromAllowedOriginSucceeds() throws Exception {
    mockMvc
        .perform(
            options("/api/courses")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "GET"))
        .andExpect(status().isOk())
        .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
  }

  @Test
  void apiResponsesIncludeContentSecurityPolicy() throws Exception {
    mockMvc
        .perform(get("/api/courses").header("Origin", "http://localhost:5173"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Security-Policy", org.hamcrest.Matchers.containsString("default-src")));
  }

  @Test
  void unauthenticatedProtectedApiStill401() throws Exception {
    mockMvc.perform(get("/api/enrollments/me")).andExpect(status().isUnauthorized());
  }
}
