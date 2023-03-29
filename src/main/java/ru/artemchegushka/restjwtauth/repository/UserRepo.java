package ru.artemchegushka.restjwtauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.artemchegushka.restjwtauth.entity.MyUser;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<MyUser, Integer> {
    Optional<MyUser> findByUsername(String username);
}
