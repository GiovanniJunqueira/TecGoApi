package com.example.tech_go_api.dto.ledger;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.tech_go_api.domain.ledger.LedgerType;
import com.example.tech_go_api.domain.payment.PaymentMethod;

public record LedgerEntryResponseDTO(
        String id,
        LedgerType type,
        String categoryId,
        String categoryName,
        BigDecimal amount,
        LocalDate date,
        String description,
        PaymentMethod paymentMethod
) {}
