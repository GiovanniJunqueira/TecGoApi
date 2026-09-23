package com.example.tech_go_api.repositories.paymentplan;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tech_go_api.domain.paymentplan.PaymentPlan;
import com.example.tech_go_api.domain.school.School;

public interface PaymentPlanRepository extends JpaRepository<PaymentPlan, String> {

    List<PaymentPlan> findBySchoolAndActive(School school, boolean active);

    List<PaymentPlan> findBySchool(School school);

    Optional<PaymentPlan> findBySchoolAndNameIgnoreCase(School school, String name);
}
