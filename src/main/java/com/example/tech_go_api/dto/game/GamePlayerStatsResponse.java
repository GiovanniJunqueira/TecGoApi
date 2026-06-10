package com.example.tech_go_api.dto.game;

public record GamePlayerStatsResponse(
        String playerId,
        String playerName,
        Integer goals,
        Boolean starter,
        String notes
) {}
