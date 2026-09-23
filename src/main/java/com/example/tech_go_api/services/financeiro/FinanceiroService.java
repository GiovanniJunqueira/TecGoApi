package com.example.tech_go_api.services.financeiro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tech_go_api.domain.payment.Payment;
import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.financeiro.FinanceiroSummaryResponse;
import com.example.tech_go_api.dto.product.SaleResponseDTO;
import com.example.tech_go_api.repositories.payment.PaymentRepository;
import com.example.tech_go_api.services.payment.PaymentPricingService;
import com.example.tech_go_api.services.product.SaleService;
import com.example.tech_go_api.services.school.SchoolResolverService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinanceiroService {

    private static final int VENDAS_RECENTES_LIMIT = 10;

    private final PaymentRepository paymentRepository;
    private final SaleService saleService;
    private final SchoolResolverService schoolResolverService;
    private final PaymentPricingService paymentPricingService;

    public FinanceiroSummaryResponse getSummary(User user, String month) {
        School school = schoolResolverService.schoolOf(user);
        YearMonth yearMonth = parseMonth(month);
        String monthKey = String.format("%d-%02d", yearMonth.getYear(), yearMonth.getMonthValue());

        List<Payment> paidPayments = paymentRepository.search(school, monthKey, true, true, null);
        BigDecimal mensalidadesRecebidas = paidPayments.stream()
                .map(Payment::getAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Payment> pendingPayments = paymentRepository.search(school, monthKey, true, false, null);
        BigDecimal mensalidadesPendentesEstimativa = pendingPayments.stream()
                .map(p -> paymentPricingService.calculateAmount(p.getProfilePlayer().getPaymentPlan(), LocalDate.now()))
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<SaleResponseDTO> sales = saleService.findAll(user, monthKey);
        BigDecimal vendasTotal = sales.stream()
                .map(SaleResponseDTO::totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new FinanceiroSummaryResponse(
                mensalidadesRecebidas,
                pendingPayments.size(),
                mensalidadesPendentesEstimativa,
                vendasTotal,
                sales.size(),
                mensalidadesRecebidas.add(vendasTotal),
                sales.stream().limit(VENDAS_RECENTES_LIMIT).toList()
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
