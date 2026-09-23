package com.example.tech_go_api.services.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.example.tech_go_api.domain.paymentplan.PaymentPlan;

@Service
public class PaymentPricingService {

    private static final int DUE_DAY = 10;

    public BigDecimal calculateAmount(PaymentPlan plan, LocalDate paidAt) {
        if (plan == null || paidAt == null) {
            return null;
        }

        boolean onTime = paidAt.getDayOfMonth() <= DUE_DAY;
        return onTime ? plan.getPriceOnTime() : plan.getPriceLate();
    }
}
