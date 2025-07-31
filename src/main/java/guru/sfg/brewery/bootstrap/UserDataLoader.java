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
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@RequiredArgsConstructor
@Component
@Slf4j
public class UserDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder = SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();


    @Transactional
    @Override
    public void run(String... args){
        loadUserData();
    }

    private void loadUserData() {
        //beer auths
        Authority createBeer = authorityRepository.save(Authority.builder().permission("beer.create").build());
        Authority readBeer = authorityRepository.save(Authority.builder().permission("beer.read").build());
        Authority updateBeer = authorityRepository.save(Authority.builder().permission("beer.update").build());
        Authority deleteBeer = authorityRepository.save(guru.sfg.brewery.domain.security.Authority.builder().permission("beer.delete").build());


        Authority createCustomer = authorityRepository.save(Authority.builder().permission("customer.create").build());
        Authority readCustomer = authorityRepository.save(Authority.builder().permission("customer.read").build());
        Authority updateCustomer = authorityRepository.save(Authority.builder().permission("customer.update").build());
        Authority deleteCustomer = authorityRepository.save(Authority.builder().permission("customer.delete").build());

        //customer brewery
        Authority createBrewery = authorityRepository.save(Authority.builder().permission("brewery.create").build());
        Authority readBrewery = authorityRepository.save(Authority.builder().permission("brewery.read").build());
        Authority updateBrewery = authorityRepository.save(Authority.builder().permission("brewery.update").build());
        Authority deleteBrewery = authorityRepository.save(Authority.builder().permission("brewery.delete").build());


        Role adminRole = roleRepository.save(Role.builder().name("ADMIN").build());
        Role customerRole = roleRepository.save(Role.builder().name("CUSTOMER").build());
        Role userRole = roleRepository.save(Role.builder().name("USER").build());

        Set<Authority> adminAuth  = new HashSet<>(Set.of(createBeer, readBeer, updateBeer, deleteBeer, createCustomer,readCustomer, updateCustomer, deleteCustomer, createBrewery, readBrewery, updateBrewery, deleteBrewery));
        Set<Authority> customerAuth = new HashSet<>(Set.of(readBeer, readCustomer, readBrewery));
        Set<Authority> userAuth = new HashSet<>(Set.of(readBeer));
        adminRole.setAuthorities(adminAuth);
        customerRole.setAuthorities(customerAuth);
        userRole.setAuthorities(userAuth);

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