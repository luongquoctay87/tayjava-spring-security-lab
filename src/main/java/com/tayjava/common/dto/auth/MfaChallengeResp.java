package com.tayjava.common.dto.auth;

/** Lesson 32: password OK but OTP still required before tokens are issued. */
public record MfaChallengeResp(String mfaToken, String channel, int expiresIn, String status) {

  public static MfaChallengeResp totp(String mfaToken, int expiresIn) {
    return new MfaChallengeResp(mfaToken, "TOTP", expiresIn, "MFA_REQUIRED");
  }
}
