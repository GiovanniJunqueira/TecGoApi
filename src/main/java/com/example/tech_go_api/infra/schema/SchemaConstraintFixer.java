package com.example.tech_go_api.infra.schema;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.example.tech_go_api.domain.staff.Permission;
import com.example.tech_go_api.domain.users.Role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * spring.jpa.hibernate.ddl-auto=update cria a check constraint de uma coluna baseada em
 * enum a partir dos valores existentes no momento em que a tabela é criada, mas não a
 * atualiza quando novos valores são adicionados ao enum depois. Sem isso, inserir uma
 * linha com um valor novo (ex: Role.STAFF, ou uma Permission nova) falha com
 * "violates check constraint" mesmo com o código já validado e implantado. Este runner
 * sincroniza cada constraint conhecida com os valores atuais do enum a cada início da
 * aplicação, então previne a mesma falha sempre que um desses enums ganhar valores novos.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SchemaConstraintFixer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        syncEnumConstraint("t_user", "role", "t_user_role_check", Role.values());
        syncEnumConstraint("t_staff_permission", "permission", "t_staff_permission_permission_check", Permission.values());
    }

    private void syncEnumConstraint(String table, String column, String constraintName, Enum<?>[] values) {
        String allowedValues = Arrays.stream(values)
                .map(value -> "'" + value.name() + "'")
                .collect(Collectors.joining(","));

        try {
            jdbcTemplate.execute("ALTER TABLE " + table + " DROP CONSTRAINT IF EXISTS " + constraintName);
            jdbcTemplate.execute("ALTER TABLE " + table + " ADD CONSTRAINT " + constraintName
                    + " CHECK (" + column + " IN (" + allowedValues + "))");
            log.info("{} sincronizada com os valores atuais: {}", constraintName, allowedValues);
        } catch (Exception e) {
            log.error("Falha ao sincronizar a constraint {}", constraintName, e);
        }
    }
}
