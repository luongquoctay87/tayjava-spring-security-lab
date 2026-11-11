package com.tayjava.demo;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tayjava.TayJavApplication;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = TayJavApplication.class)
@AutoConfigureMockMvc
class KeycloakResourceServerDemoTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void publicEndpointIsOpen() throws Exception {
    mockMvc
        .perform(get("/api/public"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("anyone can call"));
  }

  @Test
  void meWithoutTokenReturns401() throws Exception {
    mockMvc.perform(get("/api/me")).andExpect(status().isUnauthorized());
  }

  @Test
  void meWithJwtReturnsPrincipal() throws Exception {
    mockMvc
        .perform(
            get("/api/me")
                .with(
                    jwt()
                        .jwt(
                            j ->
                                j.subject("1")
                                    .claim("preferred_username", "bao.teacher")
                                    .claim(
                                        "realm_access",
                                        Map.of("roles", List.of("instructor"))))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.preferred_username").value("bao.teacher"));
  }

  @Test
  void adminWithoutRoleReturns403() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/ping")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_STUDENT"))))
        .andExpect(status().isForbidden());
  }

  @Test
  void adminWithRoleReturns200() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/ping")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("admin ok"));
  }
}
