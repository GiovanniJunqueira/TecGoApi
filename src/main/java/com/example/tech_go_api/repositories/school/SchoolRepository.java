package com.example.tech_go_api.repositories.school;


import com.example.tech_go_api.domain.school.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, String> {
    Optional<School> findByCnpj(String cnpj);
}
