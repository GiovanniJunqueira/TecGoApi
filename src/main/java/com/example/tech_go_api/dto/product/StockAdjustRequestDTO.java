package com.example.tech_go_api.dto.product;

import jakarta.validation.constraints.Min;

public record StockAdjustRequestDTO(
        @Min(value = 0, message = "O estoque não pode ser negativo") int newQuantity,
        String note
) {}
