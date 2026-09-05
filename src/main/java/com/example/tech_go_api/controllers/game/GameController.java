package com.example.tech_go_api.controllers.game;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tech_go_api.domain.game.GameCategory;
import com.example.tech_go_api.domain.game.GameType;
import com.example.tech_go_api.dto.game.GameCreateRequest;
import com.example.tech_go_api.dto.game.GameResponse;
import com.example.tech_go_api.services.game.GameService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Jogos")
@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameResponse> create(@RequestBody @Valid GameCreateRequest request) {
        return ResponseEntity.ok(gameService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<GameResponse>> findAll(
            @RequestParam(required = false) GameType type,
            @RequestParam(required = false) GameCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(gameService.findAll(type, category, startDate, endDate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(gameService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameResponse> update(@PathVariable String id, @RequestBody @Valid GameCreateRequest request) {
        return ResponseEntity.ok(gameService.update(id, request));
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<GameResponse>> findByPlayer(@PathVariable String playerId) {
        return ResponseEntity.ok(gameService.findByPlayer(playerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        gameService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
