package com.example.tech_go_api.controllers.product;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.product.SaleCreateRequestDTO;
import com.example.tech_go_api.dto.product.SaleResponseDTO;
import com.example.tech_go_api.services.product.SaleService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Vendas de Produtos")
@RestController
@RequestMapping("/api/products/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    private User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping
    public ResponseEntity<SaleResponseDTO> create(@RequestBody @Valid SaleCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleService.create(dto, currentUser()));
    }

    @GetMapping
    public ResponseEntity<List<SaleResponseDTO>> findAll(@RequestParam(required = false) String month) {
        return ResponseEntity.ok(saleService.findAll(currentUser(), month));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        saleService.delete(id, currentUser());
        return ResponseEntity.noContent().build();
    }
}
