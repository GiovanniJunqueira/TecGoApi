package com.example.tech_go_api.controllers.profileplayer;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;


import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.profileplayer.ProfilePlayerCreateRequestDTO;
import com.example.tech_go_api.dto.profileplayer.ProfilePlayerSoftDeleteRequestDTO;
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
	
	@PostMapping(path = "/createPlayer")
    public ResponseEntity<String> createProfilePlayer(@RequestBody @Valid ProfilePlayerCreateRequestDTO dto) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return profilePlayerService.createProfilePlayer(dto, user);		
    }
	
	@GetMapping (path = "/findAll")
	 public ResponseEntity<Page<ProfilePlayer>> findAllBySchool(@PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Page<ProfilePlayer> players = profilePlayerService.findAllBySchoolAndIsDeletFalse(user, pageable);
		return ResponseEntity.ok(players);		
    }
	
	@DeleteMapping(path = "/deletPlayer/{id}")
	public ResponseEntity<String> softDeleteProfilePlayer(@PathVariable String id) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
        return profilePlayerService.softDeleteProfilePlayer(id, user);
    }
}
