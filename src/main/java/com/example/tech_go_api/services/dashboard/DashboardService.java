package com.example.tech_go_api.services.dashboard;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tech_go_api.domain.payment.Payment;
import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.domain.users.profileadmin.ProfileAdmin;
import com.example.tech_go_api.dto.dashboard.DashboardSummaryResponse;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.game.GameRepository;
import com.example.tech_go_api.repositories.payment.PaymentRepository;
import com.example.tech_go_api.repositories.profileadmin.ProfileAdminRepository;
import com.example.tech_go_api.repositories.profileplayer.ProfilePlayerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProfilePlayerRepository profilePlayerRepository;
    private final GameRepository gameRepository;
    private final PaymentRepository paymentRepository;
    private final ProfileAdminRepository profileAdminRepository;

    private School schoolOf(User user) {
        ProfileAdmin profileAdmin = profileAdminRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Usuário Admin não encontrado"));
        return profileAdmin.getSchool();
    }

    public DashboardSummaryResponse getSummary(User user, String month) {
        School school = schoolOf(user);
        YearMonth yearMonth = parseMonth(month);
        String monthKey = String.format("%d-%02d", yearMonth.getYear(), yearMonth.getMonthValue());

        long totalPlayers = profilePlayerRepository.countBySchoolAndIsDeletedFalse(school);
        long totalGames = gameRepository.countBySchoolAndDateBetween(
                school, yearMonth.atDay(1), yearMonth.atEndOfMonth());

        List<Payment> allThisMonth = paymentRepository.search(school, monthKey, null, null);
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

    private YearMonth parseMonth(String month) {
        if (month == null || month.isBlank()) {
            return YearMonth.now();
        }
        try {
            return YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (Exception e) {
            return YearMonth.now();
        }
    }
}
