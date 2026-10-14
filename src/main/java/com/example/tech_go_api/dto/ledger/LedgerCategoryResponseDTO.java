package com.example.tech_go_api.dto.ledger;

import com.example.tech_go_api.domain.ledger.LedgerType;

public record LedgerCategoryResponseDTO(String id, String name, LedgerType type, boolean active) {}
