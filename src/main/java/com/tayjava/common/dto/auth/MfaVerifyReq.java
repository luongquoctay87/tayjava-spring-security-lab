package com.tayjava.common.dto.auth;

import jakarta.validation.constraints.NotBlank;

/** Lesson 32: second factor for {@code POST /api/auth/mfa/verify}. */
public record MfaVerifyReq(
    @NotBlank String mfaToken, @NotBlank String code) {

  @Override
  public String toString() {
    return "MfaVerifyReq[mfaToken=****, code=****]";
  }
}
