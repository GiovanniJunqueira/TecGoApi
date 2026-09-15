package com.example.tech_go_api.dto.aula;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record AulaGrupoCreateRequestDTO(
        @NotBlank String name,
        List<String> playerIds
) {}
