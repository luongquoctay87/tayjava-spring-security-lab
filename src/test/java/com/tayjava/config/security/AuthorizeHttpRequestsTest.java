package com.tayjava.config.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizeHttpRequestsTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void getCoursesIsPublic() throws Exception {
    mockMvc.perform(get("/api/courses")).andExpect(status().isOk());
    mockMvc.perform(get("/api/courses/1")).andExpect(status().isOk());
  }

  @Test
  void postCoursesEnforcesCourseCreateAuthority() throws Exception {
    mockMvc.perform(post("/api/courses")).andExpect(status().isUnauthorized());
    mockMvc.perform(post("/api/courses").with(TestAuthorities.student())).andExpect(status().isForbidden());
    mockMvc.perform(post("/api/courses").with(TestAuthorities.instructor())).andExpect(status().isCreated());
    mockMvc.perform(post("/api/courses").with(TestAuthorities.admin())).andExpect(status().isCreated());
  }

  @Test
  void putCoursesRequiresUpdateOwnAuthority() throws Exception {
    mockMvc.perform(put("/api/courses/1")).andExpect(status().isUnauthorized());
    mockMvc.perform(put("/api/courses/1").with(TestAuthorities.student())).andExpect(status().isForbidden());
    mockMvc.perform(put("/api/courses/1").with(TestAuthorities.instructor())).andExpect(status().isOk());
  }

  @Test
  void deleteCoursesRequiresCourseDeleteAuthority() throws Exception {
    mockMvc.perform(delete("/api/courses/1")).andExpect(status().isUnauthorized());
    mockMvc
        .perform(delete("/api/courses/1").with(TestAuthorities.instructor()))
        .andExpect(status().isForbidden());
    mockMvc.perform(delete("/api/courses/1").with(TestAuthorities.admin())).andExpect(status().isNoContent());
  }

  @Test
  void enrollmentsRequireEnrollmentReadOwn() throws Exception {
    mockMvc.perform(get("/api/enrollments/me")).andExpect(status().isUnauthorized());
    mockMvc.perform(get("/api/enrollments/me").with(TestAuthorities.student())).andExpect(status().isOk());
  }

  @Test
  void reportsRequireReportView() throws Exception {
    mockMvc.perform(get("/api/reports/revenue")).andExpect(status().isUnauthorized());
    mockMvc
        .perform(get("/api/reports/revenue").with(TestAuthorities.student()))
        .andExpect(status().isForbidden());
    mockMvc.perform(get("/api/reports/revenue").with(TestAuthorities.instructor())).andExpect(status().isOk());
  }

  @Test
  void meExposesRoleAndPermissionAuthorities() throws Exception {
    mockMvc
        .perform(get("/api/me").with(TestAuthorities.instructor()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.authorities").isArray())
        .andExpect(jsonPath("$.authorities").value(org.hamcrest.Matchers.hasItem("ROLE_INSTRUCTOR")))
        .andExpect(jsonPath("$.authorities").value(org.hamcrest.Matchers.hasItem("course:create")));
  }

  @Test
  void authLoginIsPublic() throws Exception {
    mockMvc.perform(post("/api/auth/login")).andExpect(status().isOk());
  }

  @Test
  void adminAreaStillUsesHasRole() throws Exception {
    mockMvc.perform(get("/admin")).andExpect(status().is3xxRedirection());
    mockMvc
        .perform(get("/admin").with(user("an.student").roles("STUDENT")))
        .andExpect(status().isForbidden());
    mockMvc
        .perform(get("/admin").with(user("root.admin").roles("ADMIN")))
        .andExpect(status().isOk());
  }
}
