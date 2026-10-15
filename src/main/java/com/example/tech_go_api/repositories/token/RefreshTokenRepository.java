package com.example.tech_go_api.repositories.token;

import com.example.tech_go_api.domain.token.RefreshToken;
import com.example.tech_go_api.domain.users.base.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
    void deleteByUserAndExpiryDateBefore(User user, Instant instant);
}
