package com.example.tech_go_api.controllers.aula;

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

import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.aula.AulaPresencaEntryDTO;
import com.example.tech_go_api.dto.aula.AulaSessaoCreateRequestDTO;
import com.example.tech_go_api.dto.aula.AulaSessaoResponseDTO;
import com.example.tech_go_api.services.aula.AulaSessaoService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Aulas - Sessões")
@RestController
@RequiredArgsConstructor
public class AulaSessaoController {

    private final AulaSessaoService aulaSessaoService;

    private User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping("/api/aulas/grupos/{grupoId}/sessoes")
    public ResponseEntity<AulaSessaoResponseDTO> create(@PathVariable String grupoId, @RequestBody @Valid AulaSessaoCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(aulaSessaoService.create(grupoId, dto, currentUser()));
    }

    @GetMapping("/api/aulas/grupos/{grupoId}/sessoes")
    public ResponseEntity<List<AulaSessaoResponseDTO>> findByGrupo(@PathVariable String grupoId) {
        return ResponseEntity.ok(aulaSessaoService.findByGrupo(grupoId, currentUser()));
    }

    @GetMapping("/api/aulas/sessoes/{sessaoId}")
    public ResponseEntity<AulaSessaoResponseDTO> findById(@PathVariable String sessaoId) {
        return ResponseEntity.ok(aulaSessaoService.findById(sessaoId, currentUser()));
    }

    @PutMapping("/api/aulas/sessoes/{sessaoId}/chamada")
    public ResponseEntity<AulaSessaoResponseDTO> updatePresenca(@PathVariable String sessaoId, @RequestBody List<AulaPresencaEntryDTO> entries) {
        return ResponseEntity.ok(aulaSessaoService.updatePresenca(sessaoId, entries, currentUser()));
    }

    @DeleteMapping("/api/aulas/sessoes/{sessaoId}")
    public ResponseEntity<Void> delete(@PathVariable String sessaoId) {
        aulaSessaoService.delete(sessaoId, currentUser());
        return ResponseEntity.noContent().build();
    }
}
