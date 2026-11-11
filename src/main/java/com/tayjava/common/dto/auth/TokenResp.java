package com.tayjava.common.dto.auth;

/**
 * Access + refresh token pair. {@code expiresIn} is access-token lifetime in seconds (OAuth2 RFC
 * 6749 convention). {@code refreshToken} may be null when only an access token is issued.
 */
public record TokenResp(
    String accessToken, String refreshToken, String tokenType, long expiresIn) {

  public static TokenResp bearer(String accessToken, long expiresIn) {
    return new TokenResp(accessToken, null, "Bearer", expiresIn);
  }

  public static TokenResp bearer(String accessToken, String refreshToken, long expiresIn) {
    return new TokenResp(accessToken, refreshToken, "Bearer", expiresIn);
  }
}
