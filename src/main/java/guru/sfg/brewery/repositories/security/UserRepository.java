package guru.sfg.brewery.repositories.security;

import guru.sfg.brewery.domain.security.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUsername(String username);
}