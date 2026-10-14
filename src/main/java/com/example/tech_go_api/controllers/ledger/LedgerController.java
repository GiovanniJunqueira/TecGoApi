package com.example.tech_go_api.controllers.ledger;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tech_go_api.domain.ledger.LedgerType;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.ledger.LedgerCategoryCreateRequestDTO;
import com.example.tech_go_api.dto.ledger.LedgerCategoryResponseDTO;
import com.example.tech_go_api.dto.ledger.LedgerCategoryUpdateRequestDTO;
import com.example.tech_go_api.dto.ledger.LedgerEntryRequestDTO;
import com.example.tech_go_api.dto.ledger.LedgerEntryResponseDTO;
import com.example.tech_go_api.services.ledger.LedgerService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Financeiro - Entradas e Saídas")
@RestController
@RequestMapping("/api/financeiro")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;

    private User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping("/categorias")
    public ResponseEntity<LedgerCategoryResponseDTO> createCategory(@RequestBody @Valid LedgerCategoryCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ledgerService.createCategory(dto, currentUser()));
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<LedgerCategoryResponseDTO>> findCategories(@RequestParam LedgerType type) {
        return ResponseEntity.ok(ledgerService.findCategories(type, currentUser()));
    }

    @PutMapping("/categorias/{id}")
    public ResponseEntity<LedgerCategoryResponseDTO> updateCategory(@PathVariable String id, @RequestBody @Valid LedgerCategoryUpdateRequestDTO dto) {
        return ResponseEntity.ok(ledgerService.updateCategory(id, dto, currentUser()));
    }

    @PutMapping("/categorias/{id}/inativar")
    public ResponseEntity<Void> deactivateCategory(@PathVariable String id) {
        ledgerService.deactivateCategory(id, currentUser());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lancamentos")
    public ResponseEntity<LedgerEntryResponseDTO> createEntry(@RequestBody @Valid LedgerEntryRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ledgerService.createEntry(dto, currentUser()));
    }

    @GetMapping("/lancamentos")
    public ResponseEntity<List<LedgerEntryResponseDTO>> findEntries(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) LedgerType type,
            @RequestParam(required = false) String categoryId
    ) {
        return ResponseEntity.ok(ledgerService.findEntries(month, type, categoryId, currentUser()));
    }

    @PutMapping("/lancamentos/{id}")
    public ResponseEntity<LedgerEntryResponseDTO> updateEntry(@PathVariable String id, @RequestBody @Valid LedgerEntryRequestDTO dto) {
        return ResponseEntity.ok(ledgerService.updateEntry(id, dto, currentUser()));
    }

    @DeleteMapping("/lancamentos/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable String id) {
        ledgerService.deleteEntry(id, currentUser());
        return ResponseEntity.noContent().build();
    }
}
