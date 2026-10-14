package com.example.tech_go_api.dto.ledger;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.tech_go_api.domain.payment.PaymentMethod;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LedgerEntryRequestDTO(
        @NotBlank String categoryId,
        @NotNull @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero") BigDecimal amount,
        @NotNull LocalDate date,
        String description,
        PaymentMethod paymentMethod
) {}
