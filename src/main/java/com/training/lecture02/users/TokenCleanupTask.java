package com.training.lecture02.users;

import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TokenCleanupTask {

  private final ApiUserRepository apiUserRepository;

  public TokenCleanupTask(ApiUserRepository apiUserRepository) {
    this.apiUserRepository = apiUserRepository;
  }

  @Transactional
  @Scheduled(cron = "0 0 18 * * *")
  public void clearTokens() {
    List<ApiUser> users = apiUserRepository.findAll();
    for (ApiUser user : users) {
      user.setToken(null);
    }
    apiUserRepository.saveAll(users);
  }
}
