package com.example.tech_go_api.repositories.responsible;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tech_go_api.domain.responsible.Responsible;
import com.example.tech_go_api.domain.school.School;

public interface ResponsibleRepository extends JpaRepository<Responsible, String> {

    List<Responsible> findBySchool(School school);

    List<Responsible> findBySchoolAndNameContainingIgnoreCase(School school, String name);

    @Query("SELECT DISTINCT r FROM Responsible r JOIN r.players p WHERE r.school = :school AND LOWER(p.firstname) LIKE LOWER(CONCAT('%', :studentName, '%'))")
    List<Responsible> findBySchoolAndStudentName(@Param("school") School school, @Param("studentName") String studentName);

    List<Responsible> findByPlayers_Id(String playerId);
}
