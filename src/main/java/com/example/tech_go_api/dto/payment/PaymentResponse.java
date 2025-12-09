package com.example.tech_go_api.dto.payment;

import java.time.LocalDate;

import com.example.tech_go_api.domain.payment.Payment;

public class PaymentResponse {
    private String id;
    private boolean status;
    private LocalDate paidAt;
    private String month;
    private String playerId;
    private String playerName;

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.status = payment.isStatus();
        this.paidAt = payment.getPaidAt();
        this.month = payment.getMonth();
        if (payment.getProfilePlayer() != null) {
            this.playerId = payment.getProfilePlayer().getId();
            this.playerName = payment.getProfilePlayer().getFirstname();
        }
    }

    // getters e setters
}
