package ru.xing.springcourse.petproject3rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.xing.springcourse.petproject3rest.models.MyUser;

import java.util.Optional;

public interface UserRepository extends JpaRepository<MyUser, Integer> {
    Optional<MyUser> findByUsername(String username);
}
