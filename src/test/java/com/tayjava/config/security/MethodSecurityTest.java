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
class MethodSecurityTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void ownerMayUpdateOwnCourse() throws Exception {
    mockMvc
        .perform(
            put("/api/courses/1")
                .with(TestAuthorities.instructor("bao.teacher"))
                .contentType(APPLICATION_JSON)
                .content("{\"title\":\"Owned by Bao\"}"))
        .andExpect(status().isOk());
  }

  @Test
  void otherInstructorIsForbiddenOnSameUrl() throws Exception {
    // URL layer allows course:update:own; method security denies non-owners
    mockMvc
        .perform(
            put("/api/courses/1")
                .with(TestAuthorities.instructor("chi.teacher"))
                .contentType(APPLICATION_JSON)
                .content("{\"title\":\"Hijack attempt\"}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void adminMayUpdateAnyCourse() throws Exception {
    mockMvc
        .perform(
            put("/api/courses/2")
                .with(TestAuthorities.admin())
                .contentType(APPLICATION_JSON)
                .content("{\"title\":\"Admin rewrite\"}"))
        .andExpect(status().isOk());
  }
}
