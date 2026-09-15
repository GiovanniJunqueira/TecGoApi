package com.example.tech_go_api.dto.aula;

import java.time.LocalDate;
import java.util.List;

public record AulaSessaoResponseDTO(
        String id,
        String grupoId,
        String grupoName,
        LocalDate date,
        List<AulaPresencaResponseDTO> presencas
) {}
