package com.example.tech_go_api.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.tech_go_api.domain.payment.Payment;
import com.example.tech_go_api.domain.payment.PaymentMethod;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponse {
    private String id;
    private boolean status;
    private LocalDate paidAt;
    private String month;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private String playerId;
    private String playerName;

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.status = payment.isStatus();
        this.paidAt = payment.getPaidAt();
        this.month = payment.getMonth();
        this.paymentMethod = payment.getPaymentMethod();
        this.amount = payment.getAmount();
        if (payment.getProfilePlayer() != null) {
            this.playerId = payment.getProfilePlayer().getId();
            this.playerName = payment.getProfilePlayer().getFirstname();
        }
    }
}
