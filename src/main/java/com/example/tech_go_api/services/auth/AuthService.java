package com.example.tech_go_api.services.auth;

import com.example.tech_go_api.domain.token.RefreshToken;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.auth.ChangePasswordRequestDTO;
import com.example.tech_go_api.dto.auth.LoginRequestDTO;
import com.example.tech_go_api.dto.auth.LoginResponseDTO;
import com.example.tech_go_api.dto.auth.TokenRefreshRequestDTO;
import com.example.tech_go_api.dto.auth.TokenRefreshResponseDTO;
import com.example.tech_go_api.exceptions.AuthException;
import com.example.tech_go_api.exceptions.BusinessException;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.infra.security.TokenService;
import com.example.tech_go_api.repositories.token.RefreshTokenRepository;
import com.example.tech_go_api.repositories.user.UserRepository;
import com.example.tech_go_api.cache.auth.AuthCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthCacheService authCacheService;
    private final RefreshTokenRepository refreshTokenRepository;

    public ResponseEntity<LoginResponseDTO> login(LoginRequestDTO body) {
        User user = repository.findByEmail(body.email())
                .orElseThrow(() -> new NotFoundException("Usuário com o e-mail informado não foi encontrado"));

        if (!passwordEncoder.matches(body.password(), user.getPassword())) {
            throw new BusinessException("Credenciais inválidas");
        }

        String accessToken = tokenService.generateToken(user);
        RefreshToken refreshToken = tokenService.createRefreshToken(user);
        long expiresIn = tokenService.getTokenExpirationTime();
        
        LoginResponseDTO response = new LoginResponseDTO();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken.getToken());
        response.setExpiresIn(expiresIn);
        
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<TokenRefreshResponseDTO> refreshToken(TokenRefreshRequestDTO request) {
        String requestRefreshToken = request.getRefreshToken();

        return tokenService.findByToken(requestRefreshToken)
                .map(tokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = tokenService.generateToken(user);
                    long expiresIn = tokenService.getTokenExpirationTime();
                    TokenRefreshResponseDTO response = new TokenRefreshResponseDTO();
                    response.setAccessToken(newAccessToken);
                    response.setExpiresIn(expiresIn);
                    return ResponseEntity.ok(response);
                })
                .orElseThrow(() -> new AuthException("Refresh token não encontrado!"));
    }


    public Object me(String userId) {
        return authCacheService.getMe(userId);
    }

    public void changePassword(String userId, ChangePasswordRequestDTO body) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(body.currentPassword(), user.getPassword())) {
            throw new BusinessException("Senha atual incorreta");
        }

        user.setPassword(passwordEncoder.encode(body.newPassword()));
        repository.save(user);
    }

    @Transactional
    public void logout(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

}
