package com.tayjava.config.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Lesson 40: JWT and OAuth2 login flows tested without real external IdP secrets.
 */
@SpringBootTest
@AutoConfigureMockMvc
class JwtOAuthFlowTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private JwtDecoder jwtDecoder;

  @Test
  void loginIssuesDecodableJwtWithRolesAndTenant() throws Exception {
    String token = AuthLoginSupport.accessToken(mockMvc, "an.student", "demo1234");
    Jwt jwt = jwtDecoder.decode(token);
    assertThat(jwt.getSubject()).isEqualTo("an.student");
    assertThat(jwt.getClaimAsString("tenant_id")).isEqualTo("acme");
    assertThat(jwt.getClaimAsStringList("roles")).contains("STUDENT");
  }

  @Test
  void jwtRequestPostProcessorAuthenticatesApiWithoutExternalIssuer() throws Exception {
    mockMvc
        .perform(
            get("/api/enrollments/me")
                .with(
                    jwt()
                        .jwt(
                            j ->
                                j.subject("an.student")
                                    .claim("roles", List.of("STUDENT"))
                                    .claim("permissions", List.of("enrollment:read:own"))
                                    .claim("tenant_id", "acme"))
                        .authorities(
                            new SimpleGrantedAuthority("ROLE_STUDENT"),
                            new SimpleGrantedAuthority("enrollment:read:own"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("an.student"));
  }

  @Test
  void forgedUnsignedBearerStillUnauthorized() throws Exception {
    mockMvc
        .perform(get("/api/enrollments/me").header("Authorization", "Bearer not.a.jwt"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void oauth2LoginPostProcessorOpensAuthenticatedWebHome() throws Exception {
    mockMvc
        .perform(
            get("/")
                .with(
                    oauth2Login()
                        .attributes(attrs -> attrs.put("email", "oauth.lab@example.com"))))
        .andExpect(status().isOk());
  }

  @Test
  void refreshFlowWorksAfterPasswordLogin() throws Exception {
    MvcResult login = AuthLoginSupport.loginResult(mockMvc, "an.student", "demo1234");
    String refresh = JsonPath.read(login.getResponse().getContentAsString(), "$.refreshToken");
    mockMvc
        .perform(
            post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + refresh + "\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").isString());
  }
}
