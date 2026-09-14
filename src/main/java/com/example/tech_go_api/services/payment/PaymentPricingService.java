package com.example.tech_go_api.services.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.example.tech_go_api.domain.profileplayer.PaymentPlan;

@Service
public class PaymentPricingService {

    private static final int DUE_DAY = 10;

    private static final BigDecimal PLANO_2X_ATE_DIA_10 = new BigDecimal("100.00");
    private static final BigDecimal PLANO_2X_APOS_DIA_10 = new BigDecimal("120.00");
    private static final BigDecimal PLANO_3X_ATE_DIA_10 = new BigDecimal("120.00");
    private static final BigDecimal PLANO_3X_APOS_DIA_10 = new BigDecimal("150.00");

    public BigDecimal calculateAmount(PaymentPlan plan, LocalDate paidAt) {
        if (plan == null || paidAt == null) {
            return null;
        }

        boolean onTime = paidAt.getDayOfMonth() <= DUE_DAY;

        return switch (plan) {
            case PLANO_2X -> onTime ? PLANO_2X_ATE_DIA_10 : PLANO_2X_APOS_DIA_10;
            case PLANO_3X -> onTime ? PLANO_3X_ATE_DIA_10 : PLANO_3X_APOS_DIA_10;
        };
    }
}
