package guru.sfg.brewery.security;

import guru.sfg.brewery.domain.security.Users;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserUnlockService {
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void unlockUserAccounts() {
        log.debug("Unlock User Account Check");
        List<Users> users = userRepository
                .findAllByAccountNonLockedAndLastModifiedDateIsBefore(false,
                        Timestamp.valueOf(LocalDateTime.now().minusSeconds(30)));

        if (!users.isEmpty()) {
            log.debug("Locked user accounts reset");
            users.forEach(user -> user.setAccountNonLocked(true));
            userRepository.saveAll(users);
        }

    }
}