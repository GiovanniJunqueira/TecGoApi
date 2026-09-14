package com.example.tech_go_api.dto.payment;

import com.example.tech_go_api.domain.payment.PaymentMethod;

public record MarkAsPaidRequestDTO(
        PaymentMethod paymentMethod
) {}
