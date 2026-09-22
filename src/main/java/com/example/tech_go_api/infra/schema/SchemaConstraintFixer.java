package com.example.tech_go_api.infra.schema;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.example.tech_go_api.domain.users.Role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * spring.jpa.hibernate.ddl-auto=update cria a check constraint de {@code t_user.role}
 * a partir dos valores do enum {@link Role} no momento em que a tabela é criada, mas
 * não a atualiza quando novos valores são adicionados ao enum depois. Sem isso, inserir
 * um usuário com um Role novo (ex: STAFF) falha com "violates check constraint" mesmo
 * com o código já validado e implantado.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SchemaConstraintFixer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        String allowedRoles = Arrays.stream(Role.values())
                .map(role -> "'" + role.name() + "'")
                .collect(Collectors.joining(","));

        try {
            jdbcTemplate.execute("ALTER TABLE t_user DROP CONSTRAINT IF EXISTS t_user_role_check");
            jdbcTemplate.execute("ALTER TABLE t_user ADD CONSTRAINT t_user_role_check CHECK (role IN (" + allowedRoles + "))");
            log.info("t_user_role_check sincronizada com os valores atuais de Role: {}", allowedRoles);
        } catch (Exception e) {
            log.error("Falha ao sincronizar a constraint t_user_role_check", e);
        }
    }
}
