package com.example.tech_go_api.controllers.responsible;

import java.util.List;

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

import com.example.tech_go_api.dto.responsible.ResponsibleResponse;
import com.example.tech_go_api.dto.responsible.UpdatePlayerResponsiblesRequest;
import com.example.tech_go_api.services.responsible.ResponsibleService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Responsáveis")
@RestController
@RequestMapping("/api/responsibles")
@RequiredArgsConstructor
public class ResponsibleController {

    private final ResponsibleService responsibleService;

    @GetMapping
    public ResponseEntity<List<ResponsibleResponse>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, name = "studentName") String studentName
    ) {
        return ResponseEntity.ok(responsibleService.findAll(name, studentName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponsibleResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(responsibleService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ResponsibleResponse> create(@RequestBody @Valid ResponsibleResponse request) {
        return ResponseEntity.ok(responsibleService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsibleResponse> update(@PathVariable String id,
                                                      @RequestBody @Valid ResponsibleResponse request) {
        return ResponseEntity.ok(responsibleService.update(id, request));
    }

	@GetMapping("/by-player/{playerId}")
	public ResponseEntity<List<ResponsibleResponse>> findByPlayer(@PathVariable String playerId) {
		return ResponseEntity.ok(responsibleService.findByPlayer(playerId));
	}

	@PutMapping("/by-player/{playerId}")
	public ResponseEntity<Void> updateForPlayer(@PathVariable String playerId,
	                                        @RequestBody UpdatePlayerResponsiblesRequest request) {
		responsibleService.updateResponsiblesForPlayer(playerId, request.responsibleIds());
		return ResponseEntity.noContent().build();
	}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        responsibleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
