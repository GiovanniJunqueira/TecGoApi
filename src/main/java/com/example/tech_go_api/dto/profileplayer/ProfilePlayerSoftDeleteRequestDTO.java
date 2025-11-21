package com.example.tech_go_api.dto.profileplayer;

import jakarta.validation.constraints.NotBlank;

public record ProfilePlayerSoftDeleteRequestDTO(
		@NotBlank String id
		) {
	
}
