package guru.sfg.brewery.repositories.security;

import guru.sfg.brewery.domain.security.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUsername(String username);
    List<Users> findAllByAccountNonLockedAndLastModifiedDateIsBefore(boolean locked, Timestamp timestamp);
}