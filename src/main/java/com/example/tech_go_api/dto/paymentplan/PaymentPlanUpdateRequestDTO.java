package com.example.tech_go_api.dto.paymentplan;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PaymentPlanUpdateRequestDTO(
        @NotBlank(message = "Nome do plano é obrigatório") String name,
        @NotNull(message = "Valor até o dia 10 é obrigatório") @PositiveOrZero BigDecimal priceOnTime,
        @NotNull(message = "Valor após o dia 10 é obrigatório") @PositiveOrZero BigDecimal priceLate
) {}
