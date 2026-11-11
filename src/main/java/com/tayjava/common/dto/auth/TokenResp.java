package com.tayjava.common.dto.auth;

/**
 * Lesson 23: access-token response. {@code expiresIn} is seconds (OAuth2 RFC 6749 convention).
 */
public record TokenResp(String accessToken, String tokenType, long expiresIn) {

  public static TokenResp bearer(String accessToken, long expiresIn) {
    return new TokenResp(accessToken, "Bearer", expiresIn);
  }
}
