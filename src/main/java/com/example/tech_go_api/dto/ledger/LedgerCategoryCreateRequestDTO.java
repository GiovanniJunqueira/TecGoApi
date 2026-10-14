package com.example.tech_go_api.dto.ledger;

import com.example.tech_go_api.domain.ledger.LedgerType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LedgerCategoryCreateRequestDTO(
        @NotBlank String name,
        @NotNull LedgerType type
) {}
