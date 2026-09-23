package com.example.tech_go_api.dto.financeiro;

import java.math.BigDecimal;
import java.util.List;

import com.example.tech_go_api.dto.product.SaleResponseDTO;

public record FinanceiroSummaryResponse(
        BigDecimal mensalidadesRecebidas,
        long mensalidadesPendentesQtd,
        BigDecimal mensalidadesPendentesEstimativa,
        BigDecimal vendasTotal,
        long vendasQtd,
        BigDecimal receitaTotalMes,
        List<SaleResponseDTO> vendasRecentes
) {}
