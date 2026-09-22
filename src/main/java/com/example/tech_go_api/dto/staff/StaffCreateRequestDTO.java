package com.example.tech_go_api.dto.staff;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import com.example.tech_go_api.domain.staff.Permission;
import com.example.tech_go_api.domain.staff.StaffRoleType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StaffCreateRequestDTO(
        @NotBlank String firstname,
        @NotBlank String lastname,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotNull StaffRoleType staffRole,
        String customRoleLabel,
        String phone,
        String document,
        LocalDate admissionDate,
        BigDecimal salary,
        String notes,
        Set<Permission> permissions
) {}
