package com.example.tech_go_api.repositories.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tech_go_api.domain.payment.Payment;
import com.example.tech_go_api.domain.school.School;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByProfilePlayerId(String playerId);
    List<Payment> findByMonth(String month);

    List<Payment> findByProfilePlayerSchool(School school);
    List<Payment> findByProfilePlayerSchoolAndMonth(School school, String month);
    List<Payment> findByProfilePlayerSchoolAndMonthAndStatus(School school, String month, boolean status);
}

