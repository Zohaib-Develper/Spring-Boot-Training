package com.training.lecture02.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class ApiUserServiceTest {

  @Test
  void loadUserByUsername_shouldReturnUser_whenUserExists() {
    ApiUserRepository apiUserRepository = mock(ApiUserRepository.class);
    ApiUserService apiUserService = new ApiUserService(apiUserRepository);

    ApiUser user = new ApiUser();
    user.setUsername("testuser");
    user.setPassword("encoded-password");
    user.setUserRoles("EDITOR,REPORTER");

    when(apiUserRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

    UserDetails userDetails = apiUserService.loadUserByUsername("testuser");

    assertEquals("testuser", userDetails.getUsername());
    assertEquals("encoded-password", userDetails.getPassword());
    assertEquals(2, userDetails.getAuthorities().size());
  }

  @Test
  void loadUserByUsername_shouldThrowException_whenUserNotFound() {
    ApiUserRepository apiUserRepository = mock(ApiUserRepository.class);
    ApiUserService apiUserService = new ApiUserService(apiUserRepository);

    when(apiUserRepository.findByUsername("missing")).thenReturn(Optional.empty());

    UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
        () -> apiUserService.loadUserByUsername("missing"));

    assertEquals("missing", exception.getMessage());
  }

  @Test
  void findOrCreateByEmail_shouldReturnExistingUser_whenUserExists() {
    ApiUserRepository apiUserRepository = mock(ApiUserRepository.class);
    ApiUserService apiUserService = new ApiUserService(apiUserRepository);

    ApiUser existingUser = new ApiUser();
    existingUser.setUsername("user@example.com");
    when(apiUserRepository.findByUsername("user@example.com")).thenReturn(Optional.of(existingUser));

    ApiUser result = apiUserService.findOrCreateByEmail("user@example.com");

    assertEquals(existingUser, result);
    verify(apiUserRepository, never()).save(any());
  }

  @Test
  void findOrCreateByEmail_shouldCreateNewUser_whenUserDoesNotExist() {
    ApiUserRepository apiUserRepository = mock(ApiUserRepository.class);
    ApiUserService apiUserService = new ApiUserService(apiUserRepository);

    ApiUser newUser = new ApiUser();
    newUser.setUsername("new@example.com");
    newUser.setUserRoles("EDITOR");
    when(apiUserRepository.findByUsername("new@example.com")).thenReturn(Optional.empty());
    when(apiUserRepository.save(any(ApiUser.class))).thenReturn(newUser);

    ApiUser result = apiUserService.findOrCreateByEmail("new@example.com");

    assertEquals("new@example.com", result.getUsername());
    assertEquals("EDITOR", result.getUserRoles());
    verify(apiUserRepository).save(any(ApiUser.class));
  }

  @Test
  void findOrCreateByEmail_shouldRetryOnDataIntegrityViolation() {
    ApiUserRepository apiUserRepository = mock(ApiUserRepository.class);
    ApiUserService apiUserService = new ApiUserService(apiUserRepository);

    ApiUser existingUser = new ApiUser();
    existingUser.setUsername("user@example.com");
    when(apiUserRepository.findByUsername("user@example.com"))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(existingUser));
    when(apiUserRepository.save(any(ApiUser.class)))
        .thenThrow(new DataIntegrityViolationException("Duplicate"));

    ApiUser result = apiUserService.findOrCreateByEmail("user@example.com");

    assertEquals(existingUser, result);
    verify(apiUserRepository).save(any(ApiUser.class));
  }

  @Test
  void findByUsername_shouldReturnUser() {
    ApiUserRepository apiUserRepository = mock(ApiUserRepository.class);
    ApiUserService apiUserService = new ApiUserService(apiUserRepository);

    ApiUser user = new ApiUser();
    user.setUsername("testuser");
    when(apiUserRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

    ApiUser result = apiUserService.findByUsername("testuser");

    assertEquals(user, result);
  }
}
