package com.example.tech_go_api.dto.product;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.tech_go_api.domain.payment.PaymentMethod;

public record SaleResponseDTO(
        String id,
        String productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalAmount,
        String buyerPlayerId,
        String buyerName,
        PaymentMethod paymentMethod,
        LocalDate soldAt
) {}
