package com.example.tech_go_api.dto.teacher;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.tech_go_api.domain.teacher.TeacherStatus;

public record TeacherRequest(
        String name,
        String email,
        String phone,
        String role,
        LocalDate admissionDate,
        TeacherStatus status,
        BigDecimal salary,
        String notes
) {}
