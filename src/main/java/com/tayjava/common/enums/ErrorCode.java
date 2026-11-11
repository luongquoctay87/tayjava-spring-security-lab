package com.tayjava.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** Lab subset of production API error codes. */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  SUCCESS("Success", HttpStatus.OK),
  UNAUTHORIZED("Unauthorized", HttpStatus.UNAUTHORIZED),
  FORBIDDEN("Access denied", HttpStatus.FORBIDDEN),
  INVALID_TOKEN("Invalid token", HttpStatus.UNAUTHORIZED),
  INVALID_CREDENTIALS(
      "The username or password you entered is incorrect. Please try again.",
      HttpStatus.UNAUTHORIZED);

  private final String message;
  private final HttpStatus httpStatus;
}
