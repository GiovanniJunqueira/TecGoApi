package com.example.tech_go_api.controllers;

import com.example.tech_go_api.dto.LoginRequestDTO;
import jakarta.validation.Valid;
import com.example.tech_go_api.services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticação")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO body) {
        return authService.login(body);
    }


    }

