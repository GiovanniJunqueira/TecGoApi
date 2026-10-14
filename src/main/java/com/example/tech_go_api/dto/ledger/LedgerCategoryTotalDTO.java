package com.example.tech_go_api.dto.ledger;

import java.math.BigDecimal;

public record LedgerCategoryTotalDTO(String categoryId, String categoryName, BigDecimal total, long count) {}
