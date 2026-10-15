package com.example.tech_go_api.infra.schema;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * O refresh token era @OneToOne com o usuário, então o Hibernate criou uma constraint UNIQUE em
 * t_refresh_token.user_id. Agora cada login (aparelho) tem o próprio token, e o ddl-auto=update não
 * remove constraints antigas: sem isto, o segundo login do mesmo usuário falharia.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenMultiSessionFixer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            List<String> constraints = jdbcTemplate.queryForList("""
                    SELECT con.conname
                    FROM pg_constraint con
                    JOIN pg_class rel ON rel.oid = con.conrelid
                    JOIN pg_attribute att ON att.attrelid = rel.oid AND att.attnum = ANY(con.conkey)
                    WHERE rel.relname = 't_refresh_token'
                      AND con.contype = 'u'
                      AND att.attname = 'user_id'
                    """, String.class);

            for (String name : constraints) {
                jdbcTemplate.execute("ALTER TABLE t_refresh_token DROP CONSTRAINT IF EXISTS \"" + name + "\"");
                log.info("Constraint única {} removida de t_refresh_token.user_id", name);
            }

            List<String> indexes = jdbcTemplate.queryForList("""
                    SELECT i.relname
                    FROM pg_index ix
                    JOIN pg_class t ON t.oid = ix.indrelid
                    JOIN pg_class i ON i.oid = ix.indexrelid
                    JOIN pg_attribute a ON a.attrelid = t.oid AND a.attnum = ANY(ix.indkey)
                    WHERE t.relname = 't_refresh_token'
                      AND ix.indisunique
                      AND NOT ix.indisprimary
                      AND a.attname = 'user_id'
                    """, String.class);

            for (String name : indexes) {
                jdbcTemplate.execute("DROP INDEX IF EXISTS \"" + name + "\"");
                log.info("Índice único {} removido de t_refresh_token.user_id", name);
            }
        } catch (Exception e) {
            log.error("Falha ao remover a restrição de sessão única em t_refresh_token", e);
        }
    }
}
