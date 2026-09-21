package com.example.tech_go_api.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EditPaymentRequestDTO(
        LocalDate paidAt,
        BigDecimal amount
) {}
