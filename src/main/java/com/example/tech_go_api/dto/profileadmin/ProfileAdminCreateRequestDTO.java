package com.example.tech_go_api.dto.profileadmin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ProfileAdminCreateRequestDTO(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String phone,
        @NotBlank String firstname,
        @NotBlank String lastname,
        @NotBlank String document,
        @NotBlank String schoolId
) {
}
