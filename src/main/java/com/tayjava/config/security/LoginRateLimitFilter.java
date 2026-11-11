package com.tayjava.config.security;

import com.tayjava.service.LoginAttemptService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

/** Lesson 37: reject locked username/IP before authentication work on login. */
@RequiredArgsConstructor
public class LoginRateLimitFilter extends OncePerRequestFilter {

  private final LoginAttemptService attempts;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !(request.getRequestURI().equals("/api/auth/login")
        && "POST".equalsIgnoreCase(request.getMethod()));
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String ip = request.getRemoteAddr();
    if (attempts.isIpLocked(ip)) {
      writeLocked(response);
      return;
    }
    filterChain.doFilter(request, response);
  }

  private static void writeLocked(HttpServletResponse response) throws IOException {
    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setHeader("Retry-After", "900");
    response.getWriter().write("{\"error\":\"rate_limited\",\"message\":\"Too many login attempts\"}");
  }
}
