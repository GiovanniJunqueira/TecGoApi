package com.example.tech_go_api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.tech_go_api.dto.PlayerDTO;
import com.example.tech_go_api.dto.PlayerFilter;
import com.example.tech_go_api.dto.PlayerResponse;
import com.example.tech_go_api.model.Player;
import com.example.tech_go_api.services.PlayerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class PlayerController {
	
	@Autowired
	PlayerService playerService;
	
	@PostMapping("/register")
	public ResponseEntity<?> registerPlayer(@RequestBody PlayerDTO playerDTO){
		return playerService.register(playerDTO);
	}
	
	@PostMapping("/search")
	public ResponseEntity<List<PlayerResponse>> searchPlayers(
	        @RequestBody  PlayerFilter filter,
	        @PageableDefault(size = 20, sort = "name") Pageable pageable) {

	      List<PlayerResponse> players = playerService.searchPlayers(filter, pageable);
	      return ResponseEntity.ok(players);
	    }
}
