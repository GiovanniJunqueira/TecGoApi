package com.example.tech_go_api.repositories.aula;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tech_go_api.domain.aula.AulaGrupo;
import com.example.tech_go_api.domain.aula.AulaSessao;

public interface AulaSessaoRepository extends JpaRepository<AulaSessao, String> {
    List<AulaSessao> findByGrupoOrderByDateDesc(AulaGrupo grupo);
    List<AulaSessao> findByDateBefore(LocalDate cutoff);
}
