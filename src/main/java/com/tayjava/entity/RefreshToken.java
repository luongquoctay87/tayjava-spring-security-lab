package com.tayjava.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Lesson 25: persisted refresh token. Only the SHA-256 hash is stored; rotation sets {@code
 * revokedAt} and {@code replacedByHash}.
 */
@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUser user;

  @Column(name = "token_hash", nullable = false, unique = true, length = 64)
  private String tokenHash;

  /** Shared across a login→refresh rotation chain for family-wide revoke on replay. */
  @Column(name = "family_id", nullable = false, length = 36)
  private String familyId;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "revoked_at")
  private Instant revokedAt;

  @Column(name = "replaced_by_hash", length = 64)
  private String replacedByHash;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  public boolean isActive(Instant now) {
    return revokedAt == null && expiresAt.isAfter(now);
  }
}
