package com.example.tech_go_api.dto.aula;

public record AulaPresencaResponseDTO(
        String playerId,
        String playerName,
        Boolean present
) {}
