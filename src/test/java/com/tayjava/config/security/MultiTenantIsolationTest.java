package com.tayjava.config.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MultiTenantIsolationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void acmeInstructorCanUpdateAcmeCourse() throws Exception {
    String token = AuthLoginSupport.accessToken(mockMvc, "bao.teacher", "demo1234");
    mockMvc
        .perform(
            put("/api/courses/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Acme rewrite\"}"))
        .andExpect(status().isOk());
  }

  @Test
  void acmeInstructorForbiddenOnGlobexCourse() throws Exception {
    String token = AuthLoginSupport.accessToken(mockMvc, "bao.teacher", "demo1234");
    mockMvc
        .perform(
            put("/api/courses/3")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Cross tenant hijack\"}"))
        .andExpect(status().isForbidden());
  }
}
