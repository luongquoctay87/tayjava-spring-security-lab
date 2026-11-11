package com.tayjava.common.dto.auth;

import jakarta.validation.constraints.NotBlank;

/** Lesson 25: body for {@code POST /api/auth/refresh} and logout. */
public record RefreshReq(
    @NotBlank(message = "refreshToken must not be blank") String refreshToken) {

  @Override
  public String toString() {
    return "RefreshReq[refreshToken=****]";
  }
}
