package com.example.tech_go_api.dto.product;

import java.time.LocalDate;

import com.example.tech_go_api.domain.payment.PaymentMethod;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SaleCreateRequestDTO(
        @NotBlank(message = "Produto é obrigatório") String productId,
        @Min(value = 1, message = "Quantidade deve ser ao menos 1") int quantity,
        String buyerPlayerId,
        String buyerName,
        @NotNull(message = "Forma de pagamento é obrigatória") PaymentMethod paymentMethod,
        LocalDate soldAt
) {}
