package com.example.tech_go_api.dto.paymentplan;

import java.math.BigDecimal;

public record PaymentPlanResponseDTO(
        String id,
        String name,
        BigDecimal priceOnTime,
        BigDecimal priceLate,
        boolean active
) {}
