package com.example.tech_go_api.dto.profileplayer;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

public record ProfilePlayerCreateRequestDTO(
		@NotBlank String firstname,
		@NotBlank String lastname,
		 LocalDate birthDate, 
		 String rg,
		 String cpf,
		 String phoneNumber,
		 String address,
		 String addressNumber,
		 String addressNeighborhood,
		 String addressComplement,
		 String postcode,
		 String college,
		 String collegeAddress,
		 String collegeNeighborhood,
		 String collegeComplement,
		 String collegePostcode,
		 String collegePhone,
		 String collegeSeries,
		 String collegeTime,
		 String origin,
		 String registrationId
		) {
	
}
