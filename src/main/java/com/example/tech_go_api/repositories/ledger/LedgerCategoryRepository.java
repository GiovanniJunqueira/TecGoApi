package com.example.tech_go_api.repositories.ledger;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tech_go_api.domain.ledger.LedgerCategory;
import com.example.tech_go_api.domain.ledger.LedgerType;
import com.example.tech_go_api.domain.school.School;

public interface LedgerCategoryRepository extends JpaRepository<LedgerCategory, String> {
    List<LedgerCategory> findBySchoolAndTypeAndActiveOrderByNameAsc(School school, LedgerType type, boolean active);
    Optional<LedgerCategory> findBySchoolAndTypeAndNameIgnoreCase(School school, LedgerType type, String name);
}
