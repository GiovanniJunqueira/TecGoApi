package com.example.tech_go_api.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.tech_go_api.domain.token.RefreshToken;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.exceptions.AuthException;
import com.example.tech_go_api.exceptions.BusinessException;
import com.example.tech_go_api.repositories.token.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.access.token.duration.minutes}")
    private int accessTokenDurationMinutes;
    
    @Value("${jwt.refresh.token.duration.days}")
    private int refreshTokenDurationDays;
    
    private static final long MILLIS_IN_DAY = 24 * 60 * 60 * 1000L;
    private static final long SECONDS_IN_MINUTE = 60L;

    private final RefreshTokenRepository refreshTokenRepository;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);

            Instant now = Instant.now();
            Instant expiration = generateExpirationDate();

            return JWT.create()
                    .withIssuer("tech-go-api")
                    .withSubject(user.getId())
                    .withClaim("role", user.getRole().toString())
                    .withIssuedAt(Date.from(now))
                    .withExpiresAt(expiration)
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
                        throw new BusinessException("Erro na autenticação");
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("tech-go-api")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getSubject();
                } catch (JWTVerificationException exception) {
            throw new AuthException("Token inválido ou expirado");
        }
    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now()
                .plusMinutes(accessTokenDurationMinutes)
                .toInstant(ZoneOffset.of("-03:00"));
    }
    
    
   
    public long getTokenExpirationTime() {
        return accessTokenDurationMinutes * SECONDS_IN_MINUTE;
    }
    
   
    private long getRefreshTokenDurationMs() {
        return refreshTokenDurationDays * MILLIS_IN_DAY;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseGet(() -> {
                    RefreshToken newToken = new RefreshToken();
                    newToken.setUser(user);
                    return newToken;
                });

        refreshToken.setExpiryDate(Instant.now().plusMillis(getRefreshTokenDurationMs()));

        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new AuthException("Refresh token expirado. Por favor, faça login novamente.");
        }
        return token;
    }
}
