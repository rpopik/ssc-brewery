package guru.sfg.brewery.repositories;

import guru.sfg.brewery.domain.security.LoginFailure;
import guru.sfg.brewery.domain.security.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface LoginFailureRepository extends JpaRepository<LoginFailure, Integer> {
    List<LoginFailure> findAllByUserAndCreatedDateAfter(Users user, Timestamp createdDate);
}