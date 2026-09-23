package com.example.tech_go_api.repositories.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tech_go_api.domain.product.Product;
import com.example.tech_go_api.domain.school.School;

public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findBySchoolAndActive(School school, boolean active);

    List<Product> findBySchool(School school);
}
