package com.example.tech_go_api.controllers.profileplayer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.profileplayer.ProfilePlayerCreateRequestDTO;
import com.example.tech_go_api.services.profileplayer.ProfilePlayerService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Adiministrador da Escola")
@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class ProfilePlayerController {

	private final ProfilePlayerService profilePlayerService;
	
	@PostMapping
    public ResponseEntity<String> createProfileAdmin(@RequestBody @Valid ProfilePlayerCreateRequestDTO dto) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return profilePlayerService.createProfilePlayer(dto, user);		
    }
}
