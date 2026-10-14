package com.example.tech_go_api.repositories.ledger;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tech_go_api.domain.ledger.LedgerEntry;
import com.example.tech_go_api.domain.school.School;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, String> {
    List<LedgerEntry> findBySchoolAndDateBetweenOrderByDateDescIdAsc(School school, LocalDate start, LocalDate end);
    boolean existsByCategory_Id(String categoryId);
}
