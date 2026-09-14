package com.example.tech_go_api.services.scheduled;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.example.tech_go_api.repositories.profileplayer.ProfilePlayerRepository;
import com.example.tech_go_api.services.profileplayer.ProfilePlayerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlayerScheduledJobs {

    private static final int MONTHS_INACTIVE_BEFORE_PURGE = 6;

    private final ProfilePlayerRepository profilePlayerRepository;
    private final ProfilePlayerService profilePlayerService;

    // Todo dia, às 04:00, exclui permanentemente alunos inativos há 6 meses ou mais
    @Scheduled(cron = "0 0 4 * * *")
    public void purgeLongInactivePlayers() {
        LocalDate cutoff = LocalDate.now().minusMonths(MONTHS_INACTIVE_BEFORE_PURGE);
        List<ProfilePlayer> toPurge = profilePlayerRepository.findByIsDeletedTrueAndInactiveSinceBefore(cutoff);

        for (ProfilePlayer player : toPurge) {
            profilePlayerService.purgePlayer(player);
        }

        log.info("Limpeza de alunos inativos: {} alunos excluídos permanentemente (inativos antes de {})",
                toPurge.size(), cutoff);
    }
}
