package com.example.tech_go_api.dto.profileplayer;

import jakarta.validation.constraints.NotBlank;

public record ProfilePlayerCreateRequestDTO(
		@NotBlank String firstname,
		@NotBlank String lastname
		) {
	
}
