package com.tayjava;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootTest
class TayJavApplicationTests {

  @Autowired private ApplicationContext applicationContext;

  @Test
  void contextLoads() {
    assertThat(applicationContext).isNotNull();
    assertThat(applicationContext.getBean(TayJavApplication.class)).isNotNull();
    assertThat(applicationContext.getBeanNamesForType(SecurityWebFilterChain.class)).isNotEmpty();
  }
}
