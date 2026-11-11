package com.tayjava.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class RefreshTokenTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void loginReturnsRefreshTokenAndRefreshRotates() throws Exception {
    MvcResult login = login("an.student", "demo1234");
    String access1 = JsonPath.read(login.getResponse().getContentAsString(), "$.accessToken");
    String refresh1 = JsonPath.read(login.getResponse().getContentAsString(), "$.refreshToken");
    assertThat(refresh1).isNotBlank();

    MvcResult refreshed =
        mockMvc
            .perform(
                post("/api/auth/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"refreshToken\":\"" + refresh1 + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isString())
            .andExpect(jsonPath("$.refreshToken").isString())
            .andReturn();

    String access2 = JsonPath.read(refreshed.getResponse().getContentAsString(), "$.accessToken");
    String refresh2 = JsonPath.read(refreshed.getResponse().getContentAsString(), "$.refreshToken");
    assertThat(refresh2).isNotEqualTo(refresh1);
    assertThat(access2).isNotEqualTo(access1);
  }

  @Test
  void replayOfRevokedRefreshRevokesFamily() throws Exception {
    MvcResult login = login("bao.teacher", "demo1234");
    String refresh1 = JsonPath.read(login.getResponse().getContentAsString(), "$.refreshToken");

    MvcResult rotated =
        mockMvc
            .perform(
                post("/api/auth/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"refreshToken\":\"" + refresh1 + "\"}"))
            .andExpect(status().isOk())
            .andReturn();
    String refresh2 = JsonPath.read(rotated.getResponse().getContentAsString(), "$.refreshToken");

    mockMvc
        .perform(
            post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + refresh1 + "\"}"))
        .andExpect(status().isUnauthorized());

    mockMvc
        .perform(
            post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + refresh2 + "\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void logoutRevokesRefreshToken() throws Exception {
    MvcResult login = login("root.admin", "demo1234");
    String refresh = JsonPath.read(login.getResponse().getContentAsString(), "$.refreshToken");

    mockMvc
        .perform(
            post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + refresh + "\"}"))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(
            post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + refresh + "\"}"))
        .andExpect(status().isUnauthorized());
  }

  private MvcResult login(String username, String password) throws Exception {
    return com.tayjava.config.security.AuthLoginSupport.loginResult(mockMvc, username, password);
  }
}
