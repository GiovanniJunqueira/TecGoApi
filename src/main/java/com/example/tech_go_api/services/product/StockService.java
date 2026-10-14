package com.example.tech_go_api.services.product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tech_go_api.domain.product.Product;
import com.example.tech_go_api.domain.product.StockMovement;
import com.example.tech_go_api.domain.product.StockMovementType;
import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.staff.Permission;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.product.StockAdjustRequestDTO;
import com.example.tech_go_api.dto.product.StockEntryRequestDTO;
import com.example.tech_go_api.dto.product.StockMovementResponseDTO;
import com.example.tech_go_api.exceptions.BusinessException;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.product.ProductRepository;
import com.example.tech_go_api.repositories.product.StockMovementRepository;
import com.example.tech_go_api.services.school.SchoolResolverService;
import com.example.tech_go_api.services.staff.PermissionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductRepository productRepository;
    private final StockMovementRepository movementRepository;
    private final SchoolResolverService schoolResolverService;
    private final PermissionService permissionService;

    @Transactional
    public void registerEntry(String productId, StockEntryRequestDTO dto, User user) {
        permissionService.requirePermission(user, Permission.PRODUTOS_EDITAR);
        Product product = findOwnedProduct(productId, user);
        applyDelta(product, dto.quantity(), StockMovementType.ENTRADA, clean(dto.note()), null);
    }

    @Transactional
    public void adjust(String productId, StockAdjustRequestDTO dto, User user) {
        permissionService.requirePermission(user, Permission.PRODUTOS_EDITAR);
        Product product = findOwnedProduct(productId, user);

        int delta = dto.newQuantity() - product.getStock();
        if (delta == 0) {
            return;
        }
        String note = clean(dto.note());
        applyDelta(product, delta, StockMovementType.AJUSTE, note != null ? note : "Ajuste de estoque", null);
    }

    public List<StockMovementResponseDTO> findMovements(String productId, User user) {
        permissionService.requirePermission(user, Permission.PRODUTOS_VER);
        Product product = findOwnedProduct(productId, user);
        return movementRepository.findByProduct_IdOrderByCreatedAtDesc(product.getId()).stream()
                .map(m -> new StockMovementResponseDTO(
                        m.getId(), m.getType(), m.getQuantity(), m.getBalanceAfter(), m.getNote(), m.getCreatedAt()))
                .collect(Collectors.toList());
    }

    void recordInitialStock(Product product, int quantity) {
        applyDelta(product, quantity, StockMovementType.ENTRADA, "Estoque inicial", null);
    }

    void assertAvailable(Product product, int quantity) {
        if (product.getStock() < quantity) {
            throw new BusinessException("Estoque insuficiente: restam " + product.getStock()
                    + " unidade(s) de \"" + product.getName() + "\".");
        }
    }

    void recordSale(Product product, int quantity, String saleId) {
        applyDelta(product, -quantity, StockMovementType.VENDA, null, saleId);
    }

    void restoreSale(Product product, int quantity, String saleId) {
        // Vendas anteriores ao controle de estoque nunca baixaram nada, então não há o que devolver.
        if (!movementRepository.existsBySaleIdAndType(saleId, StockMovementType.VENDA)) {
            return;
        }
        applyDelta(product, quantity, StockMovementType.ESTORNO, "Venda excluída", saleId);
    }

    private void applyDelta(Product product, int delta, StockMovementType type, String note, String saleId) {
        int newBalance = product.getStock() + delta;
        if (newBalance < 0) {
            throw new BusinessException("O estoque não pode ficar negativo.");
        }

        product.setStock(newBalance);
        productRepository.save(product);

        StockMovement movement = new StockMovement();
        movement.setSchool(product.getSchool());
        movement.setProduct(product);
        movement.setType(type);
        movement.setQuantity(delta);
        movement.setBalanceAfter(newBalance);
        movement.setNote(note);
        movement.setSaleId(saleId);
        movement.setCreatedAt(LocalDateTime.now());
        movementRepository.save(movement);
    }

    private Product findOwnedProduct(String id, User user) {
        School school = schoolResolverService.schoolOf(user);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado"));

        if (!product.getSchool().getId().equals(school.getId())) {
            throw new IllegalArgumentException("Este produto não pertence à sua escola.");
        }
        return product;
    }

    private String clean(String note) {
        return note == null || note.isBlank() ? null : note.trim();
    }
}
