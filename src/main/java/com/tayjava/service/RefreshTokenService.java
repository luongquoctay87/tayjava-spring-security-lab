package com.tayjava.service;

import com.tayjava.common.dto.auth.TokenResp;
import com.tayjava.config.security.JwtProperties;
import com.tayjava.entity.AppUser;
import com.tayjava.entity.RefreshToken;
import com.tayjava.repository.AppUserRepository;
import com.tayjava.repository.RefreshTokenRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Lesson 25: issue / rotate / revoke refresh tokens. Replay of a revoked token revokes the whole
 * family.
 */
@Service
@RequiredArgsConstructor
@Slf4j(topic = "RefreshTokenService")
public class RefreshTokenService {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  private final RefreshTokenRepository refreshTokens;
  private final AppUserRepository appUsers;
  private final AccessTokenService accessTokenService;
  private final UserDetailsService userDetailsService;
  private final JwtProperties properties;

  @Transactional
  public TokenResp issuePair(Authentication authentication) {
    AppUser user =
        appUsers
            .findByUsername(authentication.getName())
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
    String familyId = UUID.randomUUID().toString();
    String rawRefresh = newRawToken();
    persist(user, hash(rawRefresh), familyId, Instant.now());
    return TokenResp.bearer(
        accessTokenService.issue(authentication).accessToken(),
        rawRefresh,
        properties.accessTokenTtl().toSeconds());
  }

  @Transactional(noRollbackFor = ResponseStatusException.class)
  public TokenResp rotate(String rawRefreshToken) {
    Instant now = Instant.now();
    String currentHash = hash(rawRefreshToken);
    RefreshToken stored =
        refreshTokens
            .findByTokenHash(currentHash)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

    if (stored.getRevokedAt() != null) {
      refreshTokens.revokeAllActiveInFamily(stored.getFamilyId(), now);
      log.warn("Refresh replay detected for family {}", stored.getFamilyId());
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
    }
    if (stored.getExpiresAt().isBefore(now)) {
      stored.setRevokedAt(now);
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
    }

    String rawNext = newRawToken();
    String nextHash = hash(rawNext);
    stored.setRevokedAt(now);
    stored.setReplacedByHash(nextHash);

    RefreshToken next = persist(stored.getUser(), nextHash, stored.getFamilyId(), now);

    UserDetails details = userDetailsService.loadUserByUsername(next.getUser().getUsername());
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            details.getUsername(), "N/A", details.getAuthorities());

    return TokenResp.bearer(
        accessTokenService.issue(authentication).accessToken(),
        rawNext,
        properties.accessTokenTtl().toSeconds());
  }

  /** Idempotent logout: revoke the refresh-token family when the token is known. */
  @Transactional
  public void logout(String rawRefreshToken) {
    Instant now = Instant.now();
    refreshTokens
        .findByTokenHash(hash(rawRefreshToken))
        .ifPresent(rt -> refreshTokens.revokeAllActiveInFamily(rt.getFamilyId(), now));
  }

  @Transactional
  public void revokeAllForUsername(String username) {
    appUsers
        .findByUsername(username)
        .ifPresent(user -> refreshTokens.revokeAllForUser(user.getId(), Instant.now()));
  }

  private RefreshToken persist(AppUser user, String tokenHash, String familyId, Instant now) {
    RefreshToken token = new RefreshToken();
    token.setUser(user);
    token.setTokenHash(tokenHash);
    token.setFamilyId(familyId);
    token.setExpiresAt(now.plus(properties.refreshTokenTtl()));
    token.setCreatedAt(now);
    return refreshTokens.save(token);
  }

  static String newRawToken() {
    byte[] bytes = new byte[32];
    SECURE_RANDOM.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  static String hash(String rawToken) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return HexFormat.of().formatHex(digest.digest(rawToken.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException ex) {
      throw new IllegalStateException("SHA-256 not available", ex);
    }
  }
}
