package com.example.tech_go_api.services.scheduled;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.tech_go_api.domain.aula.AulaSessao;
import com.example.tech_go_api.repositories.aula.AulaSessaoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AulaScheduledJobs {

    private static final int MONTHS_OF_HISTORY = 2;

    private final AulaSessaoRepository aulaSessaoRepository;

    // Todo dia, às 05:00, apaga aulas (e suas presenças) com mais de 2 meses
    @Scheduled(cron = "0 0 5 * * *")
    public void purgeOldSessions() {
        LocalDate cutoff = LocalDate.now().minusMonths(MONTHS_OF_HISTORY);
        List<AulaSessao> toPurge = aulaSessaoRepository.findByDateBefore(cutoff);
        aulaSessaoRepository.deleteAll(toPurge);
        log.info("Limpeza de aulas antigas: {} sessões removidas (anteriores a {})", toPurge.size(), cutoff);
    }
}
