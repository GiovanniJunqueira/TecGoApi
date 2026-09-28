package com.example.tech_go_api.repositories.aula;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tech_go_api.domain.aula.AulaGrupo;
import com.example.tech_go_api.domain.aula.AulaSessao;
import com.example.tech_go_api.domain.school.School;

public interface AulaSessaoRepository extends JpaRepository<AulaSessao, String> {
    List<AulaSessao> findByGrupoOrderByDateDesc(AulaGrupo grupo);
    List<AulaSessao> findByDateBefore(LocalDate cutoff);

    @Query("SELECT s FROM AulaSessao s WHERE s.grupo.school = :school "
            + "AND (:grupoId IS NULL OR s.grupo.id = :grupoId) "
            + "AND s.date >= COALESCE(:startDate, s.date) "
            + "AND s.date <= COALESCE(:endDate, s.date) "
            + "ORDER BY s.date DESC")
    List<AulaSessao> search(
            @Param("school") School school,
            @Param("grupoId") String grupoId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
