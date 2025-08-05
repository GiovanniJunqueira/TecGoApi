package com.example.tech_go_api.services;

import com.example.tech_go_api.domain.user.Role;
import com.example.tech_go_api.domain.user.User;
import com.example.tech_go_api.dto.LoginRequestDTO;
import com.example.tech_go_api.dto.RegisterRequestDTO;
import com.example.tech_go_api.dto.ResponseDTO;
import com.example.tech_go_api.infra.security.TokenService;
import com.example.tech_go_api.exceptions.BusinessException;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

        public ResponseEntity<?> login(LoginRequestDTO body) {
        User user = repository.findByEmail(body.email())
                .orElseThrow(() -> new NotFoundException("Usuário com o e-mail informado não foi encontrado"));

        if (!passwordEncoder.matches(body.password(), user.getPassword())) {
            throw new BusinessException("Credenciais inválidas");
        }

        String token = tokenService.generateToken(user);
        return ResponseEntity.ok(new ResponseDTO(token));
    }


}
