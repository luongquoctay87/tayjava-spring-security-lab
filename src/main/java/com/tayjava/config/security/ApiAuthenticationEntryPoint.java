package com.tayjava.config.security;

import com.tayjava.common.dto.Resp;
import com.tayjava.common.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/** Lesson 26: write {@link Resp} JSON for unauthenticated API requests (401). */
@Component
@RequiredArgsConstructor
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    ErrorCode code =
        authException instanceof InvalidBearerTokenException
            ? ErrorCode.INVALID_TOKEN
            : ErrorCode.UNAUTHORIZED;
    response.setStatus(ErrorCode.UNAUTHORIZED.getHttpStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    if (authException instanceof InvalidBearerTokenException) {
      response.setHeader("WWW-Authenticate", "Bearer error=\"invalid_token\"");
    }
    objectMapper.writeValue(response.getOutputStream(), Resp.error(code));
  }
}
