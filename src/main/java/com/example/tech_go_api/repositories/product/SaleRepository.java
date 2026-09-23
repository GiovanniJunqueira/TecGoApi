package com.example.tech_go_api.repositories.product;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tech_go_api.domain.product.Sale;
import com.example.tech_go_api.domain.school.School;

public interface SaleRepository extends JpaRepository<Sale, String> {

    List<Sale> findBySchoolAndSoldAtBetweenOrderBySoldAtDesc(School school, LocalDate start, LocalDate end);

    List<Sale> findByProduct_Id(String productId);
}
