package com.example.tech_go_api.repositories.teacher;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tech_go_api.domain.teacher.Teacher;
import com.example.tech_go_api.domain.teacher.TeacherStatus;

public interface TeacherRepository extends JpaRepository<Teacher, String> {

    List<Teacher> findByNameContainingIgnoreCase(String name);

    List<Teacher> findByStatus(TeacherStatus status);
}
