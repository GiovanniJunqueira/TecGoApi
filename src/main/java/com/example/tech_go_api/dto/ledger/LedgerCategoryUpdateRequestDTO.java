package com.example.tech_go_api.dto.ledger;

import jakarta.validation.constraints.NotBlank;

public record LedgerCategoryUpdateRequestDTO(@NotBlank String name) {}
