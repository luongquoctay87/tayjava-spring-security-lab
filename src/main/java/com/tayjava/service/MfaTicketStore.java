package com.tayjava.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/** Lesson 32: opaque MFA tickets (lab in-memory; production would use Redis GETDEL). */
@Component
public class MfaTicketStore {

  public static final int MAX_ATTEMPTS = 5;
  public static final Duration DEFAULT_TTL = Duration.ofSeconds(120);

  private final Map<String, MfaTicket> tickets = new ConcurrentHashMap<>();

  public String issue(String username, String channel, Duration ttl) {
    String token = UUID.randomUUID().toString();
    tickets.put(token, new MfaTicket(username, channel, 0, Instant.now().plus(ttl)));
    return token;
  }

  public Optional<MfaTicket> peek(String mfaToken) {
    MfaTicket ticket = tickets.get(mfaToken);
    if (ticket == null || ticket.expiresAt().isBefore(Instant.now())) {
      tickets.remove(mfaToken);
      return Optional.empty();
    }
    return Optional.of(ticket);
  }

  public void recordFailure(String mfaToken) {
    tickets.computeIfPresent(
        mfaToken,
        (k, v) -> new MfaTicket(v.username(), v.channel(), v.attempts() + 1, v.expiresAt()));
  }

  public void consume(String mfaToken) {
    tickets.remove(mfaToken);
  }

  public void invalidate(String mfaToken) {
    tickets.remove(mfaToken);
  }

  public record MfaTicket(String username, String channel, int attempts, Instant expiresAt) {}
}
