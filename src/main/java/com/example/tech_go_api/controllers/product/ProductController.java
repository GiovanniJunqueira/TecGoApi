package com.example.tech_go_api.controllers.product;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.product.ProductCreateRequestDTO;
import com.example.tech_go_api.dto.product.ProductResponseDTO;
import com.example.tech_go_api.dto.product.ProductUpdateRequestDTO;
import com.example.tech_go_api.services.product.ProductService;
import com.example.tech_go_api.services.product.StockService;
import com.example.tech_go_api.dto.product.StockAdjustRequestDTO;
import com.example.tech_go_api.dto.product.StockEntryRequestDTO;
import com.example.tech_go_api.dto.product.StockMovementResponseDTO;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Produtos")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final StockService stockService;

    private User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDTO> create(
            @Valid @ModelAttribute ProductCreateRequestDTO dto,
            @RequestParam(value = "photo", required = false) MultipartFile photoFile) {
        dto.setPhotoFile(photoFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(dto, currentUser()));
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable String id,
            @Valid @ModelAttribute ProductUpdateRequestDTO dto,
            @RequestParam(value = "photo", required = false) MultipartFile photoFile) {
        dto.setPhotoFile(photoFile);
        return ResponseEntity.ok(productService.update(id, dto, currentUser()));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> findAll(
            @RequestParam(required = false, defaultValue = "ATIVOS") String status) {
        boolean active = !"INATIVOS".equalsIgnoreCase(status);
        return ResponseEntity.ok(productService.findAll(currentUser(), active));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(productService.findById(id, currentUser()));
    }

    @PutMapping("/{id}/inativar")
    public ResponseEntity<Void> deactivate(@PathVariable String id) {
        productService.deactivate(id, currentUser());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reativar")
    public ResponseEntity<Void> reactivate(@PathVariable String id) {
        productService.reactivate(id, currentUser());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/estoque/entrada")
    public ResponseEntity<ProductResponseDTO> registerStockEntry(@PathVariable String id, @RequestBody @Valid StockEntryRequestDTO dto) {
        stockService.registerEntry(id, dto, currentUser());
        return ResponseEntity.ok(productService.findById(id, currentUser()));
    }

    @PostMapping("/{id}/estoque/ajuste")
    public ResponseEntity<ProductResponseDTO> adjustStock(@PathVariable String id, @RequestBody @Valid StockAdjustRequestDTO dto) {
        stockService.adjust(id, dto, currentUser());
        return ResponseEntity.ok(productService.findById(id, currentUser()));
    }

    @GetMapping("/{id}/estoque/movimentos")
    public ResponseEntity<List<StockMovementResponseDTO>> findStockMovements(@PathVariable String id) {
        return ResponseEntity.ok(stockService.findMovements(id, currentUser()));
    }
}
