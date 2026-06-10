package com.example.tech_go_api.repositories.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tech_go_api.domain.payment.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByMonth(String month);
    List<Payment> findByProfilePlayerId(String playerId);
    List<Payment> findByMonthAndStatus(String month, boolean status);
}

