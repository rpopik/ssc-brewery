package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Users;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;


@RequiredArgsConstructor
@Component
@Slf4j
public class UserDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder = SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();


    @Override
    public void run(String... args){
        loadUserData();
    }

    private void loadUserData() {

        if (userRepository.count() > 0) {
            return; // Data already loaded
        }
        Authority admin = authorityRepository.save(Authority.builder().role("ROLE_ADMIN").build());
        Authority userRole = authorityRepository.save(Authority.builder().role("ROLE_USER").build());
        Authority customerRole = authorityRepository.save(Authority.builder().role("ROLE_CUSTOMER").build());

        userRepository.save(Users.builder()
                .username("spring")
                .password(passwordEncoder.encode("guru"))
                .authorities(Set.of(admin))
                .build());

        userRepository.save(Users.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .authorities(Set.of(userRole))
                .build());

        userRepository.save(Users.builder()
                .username("scott")
                .password(passwordEncoder.encode("tiger"))
                .authorities(Set.of(customerRole))
                .build());

        userRepository.save(Users.builder()
                .username("testApiKey")
                .password(passwordEncoder.encode("testApiKeyPassword"))
                .authorities(Set.of(customerRole))
                .build());

        log.debug("Users Loaded: " + userRepository.count());
    }
}