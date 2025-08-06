package guru.sfg.brewery.security;

import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return  userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

//        Users user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

//        return new User(user.getUsername(), user.getPassword(),user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(),
//                user.isAccountNonLocked(), convertToSpringAuthorities(user.getAuthorities()));
    }

//    private Collection<? extends GrantedAuthority> convertToSpringAuthorities(Set<Authority> authorities) {
//        if (authorities != null && !authorities.isEmpty()) {
//            return authorities.stream()
//                    .map(Authority::getPermission)
//                    .map(SimpleGrantedAuthority::new)
//                    .collect(Collectors.toSet());
//        } else {
//            return new HashSet<>();
//        }
//    }
}