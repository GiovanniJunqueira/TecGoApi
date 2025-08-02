package com.example.tech_go_api.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class PlayerFilter {

	private String name;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDateTo;

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
