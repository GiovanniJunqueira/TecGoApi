package com.example.tech_go_api.dto.profileplayer;

import java.time.LocalDate;

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
        String paymentPlanName,
        String responsibleNames,
        String aulaGrupoName,
        boolean isDeleted
) {}
