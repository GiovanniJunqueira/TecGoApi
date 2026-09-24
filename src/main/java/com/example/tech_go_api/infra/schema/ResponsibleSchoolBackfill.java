package com.example.tech_go_api.infra.schema;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Responsible ganhou o campo school (multi-tenancy) depois que vários registros já
 * existiam no banco, então esses ficaram com school_id nulo e somem de qualquer
 * consulta escopada por escola (a lista de Responsáveis, o vínculo por aluno) mesmo
 * já estando corretamente ligados a alunos via t_player_responsible. Preenche a
 * escola de cada responsável a partir de um dos alunos vinculados a ele.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ResponsibleSchoolBackfill implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            int updated = jdbcTemplate.update(
                    "UPDATE t_responsible r SET school_id = sub.school_id FROM ("
                            + "  SELECT DISTINCT ON (pr.responsible_id) pr.responsible_id, pp.school_id"
                            + "  FROM t_player_responsible pr"
                            + "  JOIN t_profile_player pp ON pp.user_id = pr.player_id"
                            + "  WHERE pp.school_id IS NOT NULL"
                            + ") sub WHERE r.id = sub.responsible_id AND r.school_id IS NULL");
            if (updated > 0) {
                log.info("{} responsáveis com school_id nulo foram vinculados à escola dos seus alunos", updated);
            }
        } catch (Exception e) {
            log.error("Falha ao preencher a escola de responsáveis legados", e);
        }
    }
}
