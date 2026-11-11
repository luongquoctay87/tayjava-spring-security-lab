package com.tayjava.config.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class StatelessApiTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void apiUnauthenticatedReturns401NotRedirect() throws Exception {
    mockMvc
        .perform(get("/api/enrollments/me"))
        .andExpect(status().isUnauthorized())
        .andExpect(header().doesNotExist("Location"));
  }

  @Test
  void apiAuthenticatedResponseHasNoSessionCookie() throws Exception {
    MockHttpServletResponse response =
        mockMvc
            .perform(get("/api/enrollments/me").with(TestAuthorities.student()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

    assertThat(response.getCookies())
        .noneMatch(cookie -> "JSESSIONID".equalsIgnoreCase(cookie.getName()));
    assertThat(response.getHeaders("Set-Cookie"))
        .noneMatch(value -> value != null && value.contains("JSESSIONID"));
  }

  @Test
  void apiPostStillWorksWithoutSession() throws Exception {
    mockMvc
        .perform(post("/api/courses").with(TestAuthorities.instructor()))
        .andExpect(status().isCreated());
  }
}
