package com.training.lecture02.users;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApiUserRepository extends JpaRepository<ApiUser, Long> {

  @Query("SELECT u FROM ApiUser u WHERE u.username = :username")
  Optional<ApiUser> findByUsername(@Param("username") String username);
}