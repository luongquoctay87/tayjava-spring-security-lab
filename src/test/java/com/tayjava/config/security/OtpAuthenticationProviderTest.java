package com.tayjava.config.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class OtpAuthenticationProviderTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void studentLoginSkipsMfaAndReturnsTokens() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"an.student\",\"password\":\"demo1234\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").isString())
        .andExpect(jsonPath("$.refreshToken").isString());
  }

  @Test
  void instructorLoginRequiresMfaThenIssuesTokens() throws Exception {
    MvcResult challenge =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"bao.teacher\",\"password\":\"demo1234\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("MFA_REQUIRED"))
            .andExpect(jsonPath("$.mfaToken").isString())
            .andReturn();

    String mfaToken = JsonPath.read(challenge.getResponse().getContentAsString(), "$.mfaToken");

    MvcResult tokens =
        mockMvc
            .perform(
                post("/api/auth/mfa/verify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"mfaToken\":\"" + mfaToken + "\",\"code\":\"123456\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isString())
            .andReturn();

    String access = JsonPath.read(tokens.getResponse().getContentAsString(), "$.accessToken");
    mockMvc
        .perform(get("/api/enrollments/me").header("Authorization", "Bearer " + access))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("bao.teacher"));
  }

  @Test
  void badOtpReturns401() throws Exception {
    MvcResult challenge =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"root.admin\",\"password\":\"demo1234\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mfaToken").isString())
            .andReturn();
    String mfaToken = JsonPath.read(challenge.getResponse().getContentAsString(), "$.mfaToken");

    mockMvc
        .perform(
            post("/api/auth/mfa/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mfaToken\":\"" + mfaToken + "\",\"code\":\"000000\"}"))
        .andExpect(status().isUnauthorized());
  }
}
