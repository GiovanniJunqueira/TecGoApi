package com.example.tech_go_api.repositories.game;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tech_go_api.domain.game.Game;
import com.example.tech_go_api.domain.game.GameCategory;
import com.example.tech_go_api.domain.game.GameType;
import com.example.tech_go_api.domain.school.School;

public interface GameRepository extends JpaRepository<Game, String> {

    @Query("SELECT g FROM Game g WHERE g.school = :school "
            + "AND (:type IS NULL OR g.type = :type) "
            + "AND (:category IS NULL OR g.category = :category) "
            + "AND (:startDate IS NULL OR g.date >= :startDate) "
            + "AND (:endDate IS NULL OR g.date <= :endDate)")
    List<Game> search(
            @Param("school") School school,
            @Param("type") GameType type,
            @Param("category") GameCategory category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    long countBySchoolAndDateBetween(School school, LocalDate startDate, LocalDate endDate);
}
