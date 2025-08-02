package com.example.tech_go_api.dto;

import java.time.LocalDate;

public record PlayerDTO(String name, 
		LocalDate birthDate, 
		String rg, 
		String cpf,
		String phone,
		String address,
		String schoolName,
		String grade,
		String schoolSchedule,
		String instagram,
		String facebook) {
}
