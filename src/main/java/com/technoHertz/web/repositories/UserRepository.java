package com.technoHertz.web.repositories;

import com.technoHertz.web.models.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserData, Long> {
    Optional<UserData> findByEmail(String email);

    void deleteByEmail(String email);
}
