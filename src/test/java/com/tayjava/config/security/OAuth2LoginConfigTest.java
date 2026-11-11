package com.tayjava.config.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OAuth2LoginConfigTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ClientRegistrationRepository clientRegistrationRepository;

  @Test
  void googleAndGithubRegistrationsAreConfigured() {
    assertThat(clientRegistrationRepository.findByRegistrationId("google")).isNotNull();
    assertThat(clientRegistrationRepository.findByRegistrationId("github")).isNotNull();
    Iterable<ClientRegistration> all = (Iterable<ClientRegistration>) clientRegistrationRepository;
    assertThat(
            StreamSupport.stream(all.spliterator(), false)
                .map(ClientRegistration::getRegistrationId)
                .toList())
        .contains("google", "github");
  }

  @Test
  void oauth2AuthorizationEndpointsRedirect() throws Exception {
    mockMvc
        .perform(get("/oauth2/authorization/google"))
        .andExpect(status().is3xxRedirection());
    mockMvc
        .perform(get("/oauth2/authorization/github"))
        .andExpect(status().is3xxRedirection());
  }
}
