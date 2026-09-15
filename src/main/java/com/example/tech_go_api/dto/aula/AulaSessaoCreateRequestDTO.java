package com.example.tech_go_api.dto.aula;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record AulaSessaoCreateRequestDTO(
        @NotNull LocalDate date
) {}
