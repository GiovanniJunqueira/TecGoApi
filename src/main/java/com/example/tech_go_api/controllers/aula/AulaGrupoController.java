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
import com.example.tech_go_api.dto.aula.AulaGrupoCreateRequestDTO;
import com.example.tech_go_api.dto.aula.AulaGrupoResponseDTO;
import com.example.tech_go_api.services.aula.AulaGrupoService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Aulas - Grupos")
@RestController
@RequestMapping("/api/aulas/grupos")
@RequiredArgsConstructor
public class AulaGrupoController {

    private final AulaGrupoService aulaGrupoService;

    private User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping
    public ResponseEntity<AulaGrupoResponseDTO> create(@RequestBody @Valid AulaGrupoCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(aulaGrupoService.create(dto, currentUser()));
    }

    @GetMapping
    public ResponseEntity<List<AulaGrupoResponseDTO>> findAll() {
        return ResponseEntity.ok(aulaGrupoService.findAll(currentUser()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AulaGrupoResponseDTO> update(@PathVariable String id, @RequestBody @Valid AulaGrupoCreateRequestDTO dto) {
        return ResponseEntity.ok(aulaGrupoService.update(id, dto, currentUser()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        aulaGrupoService.delete(id, currentUser());
        return ResponseEntity.noContent().build();
    }
}
