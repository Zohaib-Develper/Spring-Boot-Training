package com.training.lecture02.users;

import com.training.lecture02.security.Role;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApiUserService implements UserDetailsService {

  private final ApiUserRepository apiUserRepository;

  public ApiUserService(ApiUserRepository apiUserRepository) {
    this.apiUserRepository = apiUserRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<ApiUser> user = apiUserRepository.findByUsername(username);
    if (user.isEmpty()) {
      throw new UsernameNotFoundException(username);
    }
    return User.withUsername(username)
        .password(user.get().getPassword())
        .roles(user.get().getUserRoles().split(","))
        .build();
  }

  @Transactional
  public ApiUser findOrCreateByEmail(String email) {
    return apiUserRepository.findByUsername(email)
        .orElseGet(() -> {
          try {
            ApiUser newUser = new ApiUser();
            newUser.setUsername(email);
            newUser.setUserRoles(Role.EDITOR.name());
            return apiUserRepository.save(newUser);
          } catch (DataIntegrityViolationException e) {
            return apiUserRepository.findByUsername(email)
                .orElseThrow(() -> e);
          }
        });
  }

  @Cacheable(value = "users", key = "#username")
  public ApiUser findByUsername(String username) {
    return apiUserRepository.findByUsername(username).get();
  }
}