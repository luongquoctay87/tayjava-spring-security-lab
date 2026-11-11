package com.tayjava.config.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthEntryPointTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void missingAuthReturns401RespEnvelope() throws Exception {
    mockMvc
        .perform(get("/api/enrollments/me"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
        .andExpect(jsonPath("$.message").isString());
  }

  @Test
  void invalidBearerReturns401InvalidTokenEnvelope() throws Exception {
    mockMvc
        .perform(get("/api/enrollments/me").header("Authorization", "Bearer not-a-jwt"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value("INVALID_TOKEN"));
  }

  @Test
  void authenticatedWithoutPermissionReturns403RespEnvelope() throws Exception {
    String token = login("an.student", "demo1234");
    mockMvc
        .perform(delete("/api/courses/1").header("Authorization", "Bearer " + token))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value("FORBIDDEN"));
  }

  private String login(String username, String password) throws Exception {
    return AuthLoginSupport.accessToken(mockMvc, username, password);
  }
}
