package com.example.tech_go_api.controllers.auth;

import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.auth.LoginRequestDTO;
import com.example.tech_go_api.dto.auth.LoginResponseDTO;
import com.example.tech_go_api.dto.auth.TokenRefreshRequestDTO;
import com.example.tech_go_api.dto.auth.TokenRefreshResponseDTO;
import com.example.tech_go_api.services.auth.AuthService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO body) {
        return authService.login(body);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponseDTO> refreshToken(@RequestBody @Valid TokenRefreshRequestDTO body) {
        return authService.refreshToken(body);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(authService.me(user.getId()));
    }

}