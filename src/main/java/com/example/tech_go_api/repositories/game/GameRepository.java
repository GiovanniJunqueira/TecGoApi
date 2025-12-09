package com.example.tech_go_api.repositories.game;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tech_go_api.domain.game.Game;
import com.example.tech_go_api.domain.game.GameCategory;
import com.example.tech_go_api.domain.game.GameType;

public interface GameRepository extends JpaRepository<Game, String> {

    List<Game> findByTypeAndCategoryAndDateBetween(GameType type, GameCategory category, LocalDate startDate, LocalDate endDate);

    List<Game> findByTypeAndCategory(GameType type, GameCategory category);

    List<Game> findByType(GameType type);

    List<Game> findByCategory(GameCategory category);

    List<Game> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
