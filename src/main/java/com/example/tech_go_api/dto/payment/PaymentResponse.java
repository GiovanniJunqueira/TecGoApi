package com.example.tech_go_api.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.tech_go_api.domain.payment.PaymentMethod;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentResponse {
    private String id;
    private boolean status;
    private LocalDate paidAt;
    private String month;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private String playerId;
    private String playerName;
    private String responsibleName;
}
