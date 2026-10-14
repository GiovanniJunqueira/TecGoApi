package com.example.tech_go_api.repositories.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tech_go_api.domain.product.StockMovement;
import com.example.tech_go_api.domain.product.StockMovementType;

public interface StockMovementRepository extends JpaRepository<StockMovement, String> {
    List<StockMovement> findByProduct_IdOrderByCreatedAtDesc(String productId);

    boolean existsBySaleIdAndType(String saleId, StockMovementType type);
}
