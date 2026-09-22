package com.example.tech_go_api.dto.profileplayer;

import java.time.LocalDate;

import com.example.tech_go_api.domain.profileplayer.PaymentPlan;

public record PlayerReportResponseDTO(
        String id,
        String firstname,
        String lastname,
        String registrationId,
        String turma,
        LocalDate birthDate,
        String rg,
        String cpf,
        String phoneNumber,
        String address,
        String college,
        PaymentPlan paymentPlan,
        String responsibleNames,
        String aulaGrupoName,
        boolean isDeleted
) {}
