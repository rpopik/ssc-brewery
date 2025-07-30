package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.Users;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;


@RequiredArgsConstructor
@Component
@Slf4j
public class UserDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder = SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();


    @Override
    public void run(String... args){
        loadUserData();
    }

    private void loadUserData() {
        //beer auths
            final Authority createBeer = authorityRepository.save(Authority.builder().permission("beer.create").build());
            final Authority readBeer = authorityRepository.save(Authority.builder().permission("beer.read").build());
            final Authority updateBeer = authorityRepository.save(Authority.builder().permission("beer.update").build());
            final Authority deleteBeer = authorityRepository.save(guru.sfg.brewery.domain.security.Authority.builder().permission("beer.delete").build());

            Role adminRole = roleRepository.save(Role.builder().name("ADMIN").build());
            Role customerRole = roleRepository.save(Role.builder().name("CUSTOMER").build());
            Role userRole = roleRepository.save(Role.builder().name("USER").build());

            adminRole.setAuthorities(Set.of(createBeer, readBeer, updateBeer, deleteBeer));
            customerRole.setAuthorities(Set.of(readBeer));
            userRole.setAuthorities(Set.of(readBeer));

            roleRepository.saveAll(Arrays.asList(adminRole, customerRole, userRole));

        if (userRepository.count() > 0) {
            return; // Data already loaded
        }

        userRepository.save(Users.builder()
                .username("spring")
                .password(passwordEncoder.encode("guru"))
                .role(adminRole)
                .build());

        userRepository.save(Users.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .role(userRole)
                .build());

        userRepository.save(Users.builder()
                .username("scott")
                .password(passwordEncoder.encode("tiger"))
                .role(customerRole)
                .build());

        userRepository.save(Users.builder()
                .username("testApiKey")
                .password(passwordEncoder.encode("testApiKeyPassword"))
                .role(adminRole)
                .build());

        log.debug("Users Loaded: " + userRepository.count());
    }
}