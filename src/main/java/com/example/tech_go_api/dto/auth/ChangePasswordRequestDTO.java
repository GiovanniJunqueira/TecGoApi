package com.example.tech_go_api.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDTO(
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres") String newPassword
) {}
