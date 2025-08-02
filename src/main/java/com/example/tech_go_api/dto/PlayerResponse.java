package com.example.tech_go_api.dto;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerResponse {

    private UUID uuid;
    private String name;
    private LocalDate birthDate;
    private String rg;
    private String cpf;
    private String phone;
    private String address;
    private String schoolName;
    private String grade;
    private String schoolSchedule;
    private String instagram;
    private String facebook;
}