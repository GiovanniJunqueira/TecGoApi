package com.example.tech_go_api.services;

import com.example.tech_go_api.domain.user.Role;
import com.example.tech_go_api.domain.user.User;
import com.example.tech_go_api.dto.LoginRequestDTO;
import com.example.tech_go_api.dto.RegisterRequestDTO;
import com.example.tech_go_api.dto.ResponseDTO;
import com.example.tech_go_api.infra.security.TokenService;
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
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(token));
        }

        return ResponseEntity.badRequest().body("Invalid Credentials");
    }

    public ResponseEntity<?> register(RegisterRequestDTO body) {
        if (repository.findByEmail(body.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already Register.");
        }

        User newUser = new User();
        newUser.setEmail(body.email());
        newUser.setPassword(passwordEncoder.encode(body.password()));
        newUser.setPhone(body.phone());
        newUser.setFirstname(body.firstname());
        newUser.setLastname(body.lastname());
        newUser.setDocument(body.document());
        newUser.setRole(body.role());

        repository.save(newUser);

        String token = tokenService.generateToken(newUser);
        return ResponseEntity.ok(new ResponseDTO(token));
    }
}
