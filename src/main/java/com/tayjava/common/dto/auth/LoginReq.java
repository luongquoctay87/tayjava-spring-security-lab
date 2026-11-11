package com.tayjava.common.dto.auth;

import jakarta.validation.constraints.NotBlank;

/** Lesson 23: username/password body for {@code POST /api/auth/login}. */
public record LoginReq(
    @NotBlank(message = "username must not be blank") String username,
    @NotBlank(message = "password must not be blank") String password) {

  @Override
  public String toString() {
    return "LoginReq[username=" + username + ", password=****]";
  }
}
