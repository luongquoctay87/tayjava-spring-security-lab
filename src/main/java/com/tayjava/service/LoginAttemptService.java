package com.tayjava.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Lesson 37: soft-lock counters by username and IP (lab in-memory). */
@Service
public class LoginAttemptService {

  private final int maxFailures;
  private final Duration window;

  private final Map<String, Attempt> byUsername = new ConcurrentHashMap<>();
  private final Map<String, Attempt> byIp = new ConcurrentHashMap<>();

  public LoginAttemptService(
      @Value("${tayjava.security.login.max-failures:5}") int maxFailures,
      @Value("${tayjava.security.login.lock-window:PT15M}") Duration window) {
    this.maxFailures = maxFailures;
    this.window = window;
  }

  public void loginSucceeded(String username, String ip) {
    byUsername.remove(username.toLowerCase());
    if (ip != null) {
      byIp.remove(ip);
    }
  }

  public void loginFailed(String username, String ip) {
    bump(byUsername, username.toLowerCase());
    if (ip != null) {
      bump(byIp, ip);
    }
  }

  public boolean isUsernameLocked(String username) {
    return locked(byUsername.get(username.toLowerCase()));
  }

  public boolean isIpLocked(String ip) {
    return ip != null && locked(byIp.get(ip));
  }

  private void bump(Map<String, Attempt> map, String key) {
    map.compute(
        key,
        (k, existing) -> {
          Instant now = Instant.now();
          if (existing == null || existing.expiresAt().isBefore(now)) {
            return new Attempt(1, now.plus(window));
          }
          return new Attempt(existing.count() + 1, existing.expiresAt());
        });
  }

  private boolean locked(Attempt attempt) {
    if (attempt == null) {
      return false;
    }
    if (attempt.expiresAt().isBefore(Instant.now())) {
      return false;
    }
    return attempt.count() >= maxFailures;
  }

  private record Attempt(int count, Instant expiresAt) {}
}
