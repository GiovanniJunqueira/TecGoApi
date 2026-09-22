package com.example.tech_go_api.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.tech_go_api.domain.payment.PaymentMethod;

public record EditPaymentRequestDTO(
        LocalDate paidAt,
        BigDecimal amount,
        PaymentMethod paymentMethod
) {}
