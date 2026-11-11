package com.tayjava.config.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityFilterChainTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void getCoursesIsPublic() throws Exception {
    mockMvc.perform(get("/api/courses")).andExpect(status().isOk());
  }

  @Test
  void postCoursesRequiresInstructorOrAdmin() throws Exception {
    mockMvc.perform(post("/api/courses")).andExpect(status().isUnauthorized());
    mockMvc
        .perform(post("/api/courses").with(httpBasic("an.student", "demo1234")))
        .andExpect(status().isForbidden());
    mockMvc
        .perform(post("/api/courses").with(httpBasic("bao.teacher", "demo1234")))
        .andExpect(status().isCreated());
  }

  @Test
  void enrollmentsRequireAuthentication() throws Exception {
    mockMvc.perform(get("/api/enrollments/me")).andExpect(status().isUnauthorized());
    mockMvc
        .perform(get("/api/enrollments/me").with(user("an.student").roles("STUDENT")))
        .andExpect(status().isNotFound()); // no controller yet — auth passed
  }
}
