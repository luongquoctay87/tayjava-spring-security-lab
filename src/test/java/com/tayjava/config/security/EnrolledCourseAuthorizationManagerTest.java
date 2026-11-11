package com.tayjava.config.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EnrolledCourseAuthorizationManagerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void anonymousIsUnauthorized() throws Exception {
    mockMvc.perform(get("/api/courses/1/lectures")).andExpect(status().isUnauthorized());
  }

  @Test
  void enrolledStudentMayReadLectures() throws Exception {
    mockMvc
        .perform(get("/api/courses/1/lectures").with(TestAuthorities.student()))
        .andExpect(status().isOk());
  }

  @Test
  void studentNotEnrolledIsForbidden() throws Exception {
    mockMvc
        .perform(get("/api/courses/2/lectures").with(TestAuthorities.student()))
        .andExpect(status().isForbidden());
  }

  @Test
  void instructorStaffMayReadAnyCourseLectures() throws Exception {
    mockMvc
        .perform(get("/api/courses/2/lectures").with(TestAuthorities.instructor()))
        .andExpect(status().isOk());
  }
}
