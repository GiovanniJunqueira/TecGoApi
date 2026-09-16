package com.example.tech_go_api.services.scheduled;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.tech_go_api.domain.payment.Payment;
import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.repositories.payment.PaymentRepository;
import com.example.tech_go_api.repositories.profileplayer.ProfilePlayerRepository;
import com.example.tech_go_api.repositories.school.SchoolRepository;
import com.example.tech_go_api.services.payment.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentScheduledJobs {

    private static final int MONTHS_TO_KEEP_PAID_PAYMENTS = 3;

    private final SchoolRepository schoolRepository;
    private final ProfilePlayerRepository profilePlayerRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    // Todo dia 1 do mês, às 01:00, cria a cobrança pendente do mês para cada aluno ativo
    @Scheduled(cron = "0 0 1 1 * *")
    public void generateMonthlyPayments() {
        String currentMonth = String.format("%d-%02d", LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        int created = 0;

        for (School school : schoolRepository.findAll()) {
            List<ProfilePlayer> activePlayers = profilePlayerRepository.findBySchoolAndIsDeletedFalse(school);
            for (ProfilePlayer player : activePlayers) {
                if (!paymentRepository.existsByProfilePlayerIdAndMonth(player.getId(), currentMonth)) {
                    paymentService.createPayment(player, currentMonth);
                    created++;
                }
            }
        }

        log.info("Geração automática de mensalidades ({}): {} pagamentos criados", currentMonth, created);
    }

    // Todo dia, às 03:00, apaga pagamentos já quitados com mais de 3 meses
    @Scheduled(cron = "0 0 3 * * *")
    public void purgeOldPaidPayments() {
        YearMonth cutoff = YearMonth.now().minusMonths(MONTHS_TO_KEEP_PAID_PAYMENTS);
        int deleted = 0;

        for (Payment payment : paymentRepository.findByStatus(true)) {
            YearMonth paymentMonth = parseMonth(payment.getMonth());
            if (paymentMonth != null && paymentMonth.isBefore(cutoff)) {
                paymentRepository.delete(payment);
                deleted++;
            }
        }

        log.info("Limpeza de pagamentos antigos: {} registros removidos (anteriores a {})", deleted, cutoff);
    }

    private YearMonth parseMonth(String month) {
        if (month == null || !month.contains("-")) {
            return null;
        }
        try {
            String[] parts = month.split("-");
            return YearMonth.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
