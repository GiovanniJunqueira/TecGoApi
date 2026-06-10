package com.example.tech_go_api.repositories.game;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tech_go_api.domain.game.GamePlayerStats;

public interface GamePlayerStatsRepository extends JpaRepository<GamePlayerStats, String> {

    List<GamePlayerStats> findByPlayerId(String playerId);
}
