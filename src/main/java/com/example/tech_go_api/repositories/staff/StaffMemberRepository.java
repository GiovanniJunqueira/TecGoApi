package com.example.tech_go_api.repositories.staff;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.staff.StaffMember;

public interface StaffMemberRepository extends JpaRepository<StaffMember, String> {
    List<StaffMember> findBySchool(School school);
}
