package com.example.tech_go_api.dto.staff;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import com.example.tech_go_api.domain.staff.Permission;
import com.example.tech_go_api.domain.staff.StaffRoleType;
import com.example.tech_go_api.domain.staff.StaffStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StaffUpdateRequestDTO(
        @NotBlank String firstname,
        @NotBlank String lastname,
        String password,
        @NotNull StaffRoleType staffRole,
        String customRoleLabel,
        String phone,
        String document,
        LocalDate admissionDate,
        BigDecimal salary,
        String notes,
        StaffStatus status,
        Set<Permission> permissions
) {}
