package com.example.tech_go_api.dto;

import com.example.tech_go_api.domain.users.Role;

public record RegisterRequestDTO(
        String email,
        String password,
        String phone,
        String firstname,
        String lastname,
        String document,
        Role role
) {}
