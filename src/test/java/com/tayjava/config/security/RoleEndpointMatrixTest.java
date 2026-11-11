package com.tayjava.config.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Lesson 39: role × endpoint matrix with MockMvc security post-processors and {@code @WithMockUser}.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RoleEndpointMatrixTest {

  @Autowired private MockMvc mockMvc;

  @ParameterizedTest(name = "{0} {1} as {2} → {3}")
  @CsvSource({
    "GET,/api/courses,anonymous,200",
    "GET,/api/enrollments/me,anonymous,401",
    "GET,/api/enrollments/me,student,200",
    "POST,/api/courses,student,403",
    "POST,/api/courses,instructor,201",
    "POST,/api/courses,admin,201"
  })
  void apiMatrix(String method, String path, String actor, int expected) throws Exception {
    MockHttpServletRequestBuilder req =
        "POST".equals(method)
            ? post(path).contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Matrix\"}")
            : get(path);
    req =
        switch (actor) {
          case "student" -> req.with(TestAuthorities.student());
          case "instructor" -> req.with(TestAuthorities.instructor());
          case "admin" -> req.with(TestAuthorities.admin());
          default -> req;
        };
    mockMvc.perform(req).andExpect(status().is(expected));
  }

  @Test
  void mockMvcUserAdminReachesDeleteAuthorization() throws Exception {
    mockMvc
        .perform(delete("/api/courses/99").with(TestAuthorities.admin()))
        .andExpect(status().isNotFound());
  }

  @Test
  void mockMvcUserStudentForbiddenOnDelete() throws Exception {
    mockMvc
        .perform(delete("/api/courses/1").with(TestAuthorities.student()))
        .andExpect(status().isForbidden());
  }

  @Test
  void mockMvcUserAdminCanOpenWebAdmin() throws Exception {
    mockMvc.perform(get("/admin").with(user("root.admin").roles("ADMIN"))).andExpect(status().isOk());
  }

  @Test
  void mockMvcUserStudentForbiddenOnWebAdmin() throws Exception {
    mockMvc
        .perform(get("/admin").with(user("an.student").roles("STUDENT")))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "root.admin", roles = "ADMIN")
  void withMockUserPopulatesSecurityContextForWebAdmin() throws Exception {
    // Prefer request post-processors for filter-chain tests; @WithMockUser still documents the
    // annotation style for service-layer / simpler web checks.
    mockMvc.perform(get("/admin").with(user("root.admin").roles("ADMIN"))).andExpect(status().isOk());
  }

  @Test
  @WithAnonymousUser
  void anonymousWebAdminRedirectsToLogin() throws Exception {
    mockMvc.perform(get("/admin")).andExpect(status().is3xxRedirection());
  }
}
