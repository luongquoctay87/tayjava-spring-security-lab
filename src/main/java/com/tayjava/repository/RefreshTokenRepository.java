package com.tayjava.repository;

import com.tayjava.entity.RefreshToken;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByTokenHash(String tokenHash);

  @Modifying(clearAutomatically = true)
  @Query(
      """
      update RefreshToken rt
         set rt.revokedAt = :now
       where rt.familyId = :familyId
         and rt.revokedAt is null
      """)
  int revokeAllActiveInFamily(@Param("familyId") String familyId, @Param("now") Instant now);

  @Modifying(clearAutomatically = true)
  @Query(
      """
      update RefreshToken rt
         set rt.revokedAt = :now
       where rt.user.id = :userId
         and rt.revokedAt is null
      """)
  int revokeAllForUser(@Param("userId") Long userId, @Param("now") Instant now);

  @Modifying
  @Query("delete from RefreshToken rt where rt.expiresAt < :cutoff")
  int deleteExpiredBefore(@Param("cutoff") Instant cutoff);
}
