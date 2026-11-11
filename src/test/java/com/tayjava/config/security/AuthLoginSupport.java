package com.tayjava.config.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/** Completes password + optional lab OTP so elevated roles still get tokens in tests. */
public final class AuthLoginSupport {

  static final String LAB_OTP = "123456";

  private AuthLoginSupport() {}

  public static String accessToken(MockMvc mockMvc, String username, String password) throws Exception {
    return JsonPath.read(loginBody(mockMvc, username, password), "$.accessToken");
  }

  public static MvcResult loginResult(MockMvc mockMvc, String username, String password)
      throws Exception {
    MvcResult step1 =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"username\":\""
                            + username
                            + "\",\"password\":\""
                            + password
                            + "\"}"))
            .andExpect(status().isOk())
            .andReturn();
    String body = step1.getResponse().getContentAsString();
    if (body.contains("\"mfaToken\"")) {
      String mfaToken = JsonPath.read(body, "$.mfaToken");
      return mockMvc
          .perform(
              post("/api/auth/mfa/verify")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\"mfaToken\":\"" + mfaToken + "\",\"code\":\"" + LAB_OTP + "\"}"))
          .andExpect(status().isOk())
          .andReturn();
    }
    return step1;
  }

  private static String loginBody(MockMvc mockMvc, String username, String password)
      throws Exception {
    return loginResult(mockMvc, username, password).getResponse().getContentAsString();
  }
}
