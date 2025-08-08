package com.example.tech_go_api.repositories.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tech_go_api.domain.users.base.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
}
