package com.tayjava.config.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class JwtBearerAuthTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private FilterChainProxy filterChainProxy;

  @Test
  void apiChainContainsBearerTokenAuthenticationFilter() {
    List<SecurityFilterChain> chains = filterChainProxy.getFilterChains();
    assertThat(chains).isNotEmpty();
    boolean found =
        chains.stream()
            .flatMap(chain -> chain.getFilters().stream())
            .anyMatch(BearerTokenAuthenticationFilter.class::isInstance);
    assertThat(found).as("oauth2ResourceServer should register BearerTokenAuthenticationFilter").isTrue();
  }

  @Test
  void bearerTokenFromLoginAccessesProtectedApi() throws Exception {
    String token = login("an.student", "demo1234");

    mockMvc
        .perform(get("/api/enrollments/me").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("an.student"));
  }

  @Test
  void instructorBearerCanCreateCourse() throws Exception {
    String token = login("bao.teacher", "demo1234");

    mockMvc
        .perform(
            post("/api/courses")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"JWT Filter Course\"}"))
        .andExpect(status().isCreated());
  }

  @Test
  void invalidBearerTokenReturns401WithWwwAuthenticate() throws Exception {
    mockMvc
        .perform(get("/api/enrollments/me").header("Authorization", "Bearer not-a-real-jwt"))
        .andExpect(status().isUnauthorized())
        .andExpect(header().string("WWW-Authenticate", org.hamcrest.Matchers.containsString("Bearer")));
  }

  private String login(String username, String password) throws Exception {
    return AuthLoginSupport.accessToken(mockMvc, username, password);
  }
}
