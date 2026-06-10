package com.example.tech_go_api.repositories.responsible;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tech_go_api.domain.responsible.Responsible;

public interface ResponsibleRepository extends JpaRepository<Responsible, String> {

    List<Responsible> findByNameContainingIgnoreCase(String name);

    @Query("SELECT DISTINCT r FROM Responsible r JOIN r.players p WHERE LOWER(p.firstname) LIKE LOWER(CONCAT('%', :studentName, '%'))")
    List<Responsible> findByStudentName(@Param("studentName") String studentName);
}
