package com.example.tech_go_api.dto.product;

import java.time.LocalDateTime;

import com.example.tech_go_api.domain.product.StockMovementType;

public record StockMovementResponseDTO(
        String id,
        StockMovementType type,
        int quantity,
        int balanceAfter,
        String note,
        LocalDateTime createdAt
) {}
