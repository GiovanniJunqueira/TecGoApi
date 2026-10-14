package com.example.tech_go_api.dto.product;

import jakarta.validation.constraints.Min;

public record StockEntryRequestDTO(
        @Min(value = 1, message = "A quantidade deve ser ao menos 1") int quantity,
        String note
) {}
