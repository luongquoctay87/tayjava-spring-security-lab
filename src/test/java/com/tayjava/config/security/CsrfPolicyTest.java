package com.tayjava.config.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CsrfPolicyTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void apiPostDoesNotRequireCsrf() throws Exception {
    mockMvc
        .perform(post("/api/courses").with(TestAuthorities.instructor()))
        .andExpect(status().isCreated());
  }

  @Test
  void loginPageIsPublic() throws Exception {
    mockMvc.perform(get("/login")).andExpect(status().isOk());
  }

  @Test
  void webPostWithoutCsrfIsForbidden() throws Exception {
    mockMvc.perform(post("/logout")).andExpect(status().isForbidden());
    mockMvc.perform(post("/logout").with(csrf())).andExpect(status().is3xxRedirection());
  }
}
