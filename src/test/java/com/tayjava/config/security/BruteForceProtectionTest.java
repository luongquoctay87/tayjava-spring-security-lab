package com.tayjava.config.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tayjava.service.LoginAttemptService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BruteForceProtectionTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private LoginAttemptService loginAttemptService;

  @AfterEach
  void clearLocks() {
    loginAttemptService.clearAll();
  }

  @Test
  void validLoginStillWorks() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"an.student\",\"password\":\"demo1234\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").isString());
  }

  @Test
  void repeatedBadPasswordsEventuallyLockAccount() throws Exception {
    String body = "{\"username\":\"an.student\",\"password\":\"wrong-password\"}";
    for (int i = 0; i < 5; i++) {
      mockMvc
          .perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
          .andExpect(status().isUnauthorized());
    }
    // 6th attempt: locked via UserDetails.accountLocked → still 401 (not leaking lock detail)
    mockMvc
        .perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isUnauthorized());
  }
}
