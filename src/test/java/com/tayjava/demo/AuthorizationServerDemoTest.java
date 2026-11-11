package com.tayjava.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tayjava.TayJavApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(
    classes = TayJavApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthorizationServerDemoTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private RegisteredClientRepository registeredClientRepository;

  @Test
  void registeredClientsArePresent() {
    assertThat(registeredClientRepository.findByClientId("tayjava-web")).isNotNull();
    assertThat(registeredClientRepository.findByClientId("tayjava-mobile")).isNotNull();
    assertThat(registeredClientRepository.findByClientId("tayjava-partner-sync")).isNotNull();
  }

  @Test
  void openIdConfigurationExposesIssuerAndTokenEndpoint() throws Exception {
    mockMvc
        .perform(get("/.well-known/openid-configuration"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.issuer").value("http://localhost:9000"))
        .andExpect(jsonPath("$.token_endpoint").value("http://localhost:9000/oauth2/token"))
        .andExpect(jsonPath("$.jwks_uri").value("http://localhost:9000/oauth2/jwks"));
  }

  @Test
  void clientCredentialsIssuesAccessToken() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                post("/oauth2/token")
                    .with(httpBasic("tayjava-partner-sync", "lab-partner-secret"))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("grant_type", "client_credentials")
                    .param("scope", "enrollments.read"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.access_token").isNotEmpty())
            .andExpect(jsonPath("$.token_type").value("Bearer"))
            .andReturn();

    assertThat(result.getResponse().getContentAsString()).contains("access_token");
  }

  @Test
  void clientCredentialsWithWrongSecretReturns401() throws Exception {
    mockMvc
        .perform(
            post("/oauth2/token")
                .with(httpBasic("tayjava-partner-sync", "wrong-secret"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "client_credentials")
                .param("scope", "enrollments.read"))
        .andExpect(status().isUnauthorized());
  }
}
