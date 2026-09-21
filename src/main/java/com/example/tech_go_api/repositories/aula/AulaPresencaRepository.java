package com.example.tech_go_api.repositories.aula;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tech_go_api.domain.aula.AulaPresenca;

public interface AulaPresencaRepository extends JpaRepository<AulaPresenca, String> {
    List<AulaPresenca> findByPlayer_Id(String playerId);
}
