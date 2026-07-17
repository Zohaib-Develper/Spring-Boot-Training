package com.training.lecture02.users;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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

    public String generateToken(String username) {
        ApiUser user = apiUserRepository.findByUsername(username).get();
        user.setToken(UUID.randomUUID().toString());
        apiUserRepository.save(user);
        return user.getToken();
    }

    public ApiUser findByToken(String token) {
        Optional<ApiUser> user = apiUserRepository.findByToken(token);
        if(user.isEmpty()) {
            throw new OAuth2AuthenticationException("Invalid");
        }
        return user.get();
    }
}