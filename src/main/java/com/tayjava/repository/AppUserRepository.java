package com.tayjava.repository;

import com.tayjava.entity.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

  @Query(
      """
      select distinct u from AppUser u
      left join fetch u.roles r
      left join fetch r.permissions
      where u.username = :username
      """)
  Optional<AppUser> findByUsernameWithRolesAndPermissions(@Param("username") String username);

  Optional<AppUser> findByUsername(String username);

  boolean existsByUsername(String username);
}
