package com.example.tech_go_api.infra.schema;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * t_payment.month é comparado por igualdade de string em toda busca/agregação
 * (filtro por mês, Dashboard, Financeiro), então "2026-9" e "2026-09" contam como
 * meses diferentes mesmo sendo o mesmo mês. Alguns registros foram criados sem o
 * zero à esquerda (ex: formulário de lançar pagamento aceitava texto livre) e
 * ficavam de fora dessas contagens sem gerar nenhum erro visível. Corrigido na
 * origem (PaymentService normaliza o mês antes de salvar), mas os registros já
 * existentes precisam desse ajuste único no banco.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentMonthFormatFixer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            int updated = jdbcTemplate.update(
                    "UPDATE t_payment SET month = regexp_replace(month, '-([0-9])$', '-0\\1') "
                            + "WHERE month ~ '^[0-9]{4}-[0-9]$'");
            if (updated > 0) {
                log.info("{} registros de t_payment.month normalizados para o formato yyyy-MM", updated);
            }
        } catch (Exception e) {
            log.error("Falha ao normalizar t_payment.month", e);
        }
    }
}
