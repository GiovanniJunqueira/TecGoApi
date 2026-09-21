package com.example.tech_go_api.dto.payment;

import java.time.LocalDate;

import com.example.tech_go_api.domain.payment.PaymentMethod;

public record MarkAsPaidRequestDTO(
        PaymentMethod paymentMethod,
        LocalDate paidAt
) {}
