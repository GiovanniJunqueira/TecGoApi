package com.example.tech_go_api.services.dashboard;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tech_go_api.domain.payment.Payment;
import com.example.tech_go_api.dto.dashboard.DashboardSummaryResponse;
import com.example.tech_go_api.repositories.game.GameRepository;
import com.example.tech_go_api.repositories.payment.PaymentRepository;
import com.example.tech_go_api.repositories.profileplayer.ProfilePlayerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProfilePlayerRepository profilePlayerRepository;
    private final GameRepository gameRepository;
    private final PaymentRepository paymentRepository;

    public DashboardSummaryResponse getSummary() {
        long totalPlayers = profilePlayerRepository.count();
        long totalGames = gameRepository.count();

        String currentMonth = LocalDate.now().getYear() + "-" + LocalDate.now().getMonthValue();

        List<Payment> allThisMonth = paymentRepository.findByMonth(currentMonth);
        long totalPaymentsThisMonth = allThisMonth.size();
        long totalPaymentsPaidThisMonth = allThisMonth.stream().filter(Payment::isStatus).count();
        long totalPaymentsPendingThisMonth = totalPaymentsThisMonth - totalPaymentsPaidThisMonth;

        return new DashboardSummaryResponse(
                totalPlayers,
                totalGames,
                totalPaymentsThisMonth,
                totalPaymentsPaidThisMonth,
                totalPaymentsPendingThisMonth
        );
    }
}
