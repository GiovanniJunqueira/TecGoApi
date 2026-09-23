package com.example.tech_go_api.dto.product;

import java.math.BigDecimal;

public record ProductResponseDTO(
        String id,
        String name,
        String description,
        BigDecimal price,
        String photoUrl,
        boolean active
) {}
