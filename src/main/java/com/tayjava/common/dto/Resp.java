package com.tayjava.common.dto;

import com.tayjava.common.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Standard API envelope used by security handlers and (later) controllers.
 *
 * @param <T> payload type
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Resp<T> {
  private boolean success;
  private String code;
  private String message;
  private T data;

  public static <T> Resp<T> success(T data) {
    return Resp.<T>builder()
        .success(true)
        .code(ErrorCode.SUCCESS.name())
        .message(ErrorCode.SUCCESS.getMessage())
        .data(data)
        .build();
  }

  public static <T> Resp<T> error(ErrorCode errorCode) {
    return error(errorCode, errorCode.getMessage());
  }

  public static <T> Resp<T> error(ErrorCode errorCode, String message) {
    return Resp.<T>builder().success(false).code(errorCode.name()).message(message).build();
  }
}
