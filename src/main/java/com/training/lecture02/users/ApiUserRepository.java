package com.training.lecture02.users;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiUserRepository extends JpaRepository<ApiUser, Long> {

  Optional<ApiUser> findByUsername(String username);
}
