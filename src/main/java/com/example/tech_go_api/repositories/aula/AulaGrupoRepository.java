package com.example.tech_go_api.repositories.aula;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tech_go_api.domain.aula.AulaGrupo;
import com.example.tech_go_api.domain.school.School;

public interface AulaGrupoRepository extends JpaRepository<AulaGrupo, String> {
    List<AulaGrupo> findBySchool(School school);
    List<AulaGrupo> findByPlayers_Id(String playerId);
}
