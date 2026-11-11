package com.tayjava.config.security;

import com.tayjava.service.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Lesson 37: count failures without changing AuthenticationProvider code. */
@Component
@RequiredArgsConstructor
public class AuthenticationAttemptListener {

  private final LoginAttemptService attempts;

  @EventListener
  public void onSuccess(AuthenticationSuccessEvent event) {
    attempts.loginSucceeded(event.getAuthentication().getName(), clientIp());
  }

  @EventListener
  public void onFailure(AbstractAuthenticationFailureEvent event) {
    String username =
        event.getAuthentication() != null ? event.getAuthentication().getName() : "unknown";
    attempts.loginFailed(username, clientIp());
  }

  private static String clientIp() {
    ServletRequestAttributes attrs =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attrs == null) {
      return null;
    }
    HttpServletRequest request = attrs.getRequest();
    return request.getRemoteAddr();
  }
}
