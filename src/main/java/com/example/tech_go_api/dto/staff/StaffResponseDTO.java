package com.example.tech_go_api.dto.staff;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import com.example.tech_go_api.domain.staff.Permission;
import com.example.tech_go_api.domain.staff.StaffRoleType;
import com.example.tech_go_api.domain.staff.StaffStatus;

public record StaffResponseDTO(
        String id,
        String firstname,
        String lastname,
        String email,
        StaffRoleType staffRole,
        String customRoleLabel,
        String phone,
        String document,
        LocalDate admissionDate,
        BigDecimal salary,
        String notes,
        StaffStatus status,
        Set<Permission> permissions
) {}
