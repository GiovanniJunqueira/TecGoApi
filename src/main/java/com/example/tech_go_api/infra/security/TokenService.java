package com.example.tech_go_api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.tech_go_api.domain.user.User;
import com.example.tech_go_api.exceptions.AuthException;
import com.example.tech_go_api.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenService {

    @Value("${JWT_SECRET}")
    private String SECRET;

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
                .plusMinutes(15)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}
