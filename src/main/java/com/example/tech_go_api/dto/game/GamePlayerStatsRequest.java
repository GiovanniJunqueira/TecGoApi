package com.example.tech_go_api.dto.game;

public record GamePlayerStatsRequest(
        String playerId,
        Integer goals,
        Boolean starter,
        String notes
) {}
