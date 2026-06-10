package com.example.tech_go_api.dto.game;

import java.time.LocalDate;
import java.util.List;

import com.example.tech_go_api.domain.game.GameCategory;
import com.example.tech_go_api.domain.game.GameType;

public record GameCreateRequest(
        GameType type,
        GameCategory category,
        String opponent,
        LocalDate date,
        Integer homeScore,
        Integer awayScore,
        String location,
        List<GamePlayerStatsRequest> players
) {}
