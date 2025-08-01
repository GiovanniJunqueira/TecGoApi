package com.example.tech_go_api.controllers;

import com.example.tech_go_api.dto.LoginRequestDTO;
import com.example.tech_go_api.dto.RegisterRequestDTO;
import com.example.tech_go_api.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO body) {
        return authService.login(body);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO body) {
        return authService.register(body);
    }
}
