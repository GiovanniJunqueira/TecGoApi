package com.example.tech_go_api.dto.aula;

import java.util.List;

public record AulaGrupoResponseDTO(
        String id,
        String name,
        List<AulaPlayerSummaryDTO> players
) {}
