package com.tayjava.config.security;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DomainObjectSecurityTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void ownerHasPermissionWriteOnOwnCourse() throws Exception {
    mockMvc
        .perform(
            put("/api/courses/1")
                .with(TestAuthorities.instructor("bao.teacher"))
                .contentType(APPLICATION_JSON)
                .content("{\"title\":\"Domain object ok\"}"))
        .andExpect(status().isOk());
  }

  @Test
  void nonOwnerDeniedEvenWithInstructorRole() throws Exception {
    mockMvc
        .perform(
            put("/api/courses/1")
                .with(TestAuthorities.instructor("chi.teacher"))
                .contentType(APPLICATION_JSON)
                .content("{\"title\":\"Domain object denied\"}"))
        .andExpect(status().isForbidden());
  }
}
