package com.tayjava.demo.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class FilterChainProbeFilterTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private SecurityFilterChain securityFilterChain;

  @BeforeEach
  void reset() {
    FilterChainProbeFilter.HIT.set(false);
  }

  @Test
  void defaultSecurityFilterChainExists() {
    assertThat(securityFilterChain).isNotNull();
    assertThat(securityFilterChain.getFilters()).isNotEmpty();
  }

  @Test
  void probeFilterRunsOnRequest() throws Exception {
    mockMvc.perform(get("/api/probe")).andExpect(status().isOk()).andExpect(content().string("ok"));
    assertThat(FilterChainProbeFilter.HIT).isTrue();
  }
}
