package com.example.tech_go_api.dto.teacher;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.tech_go_api.domain.teacher.Teacher;
import com.example.tech_go_api.domain.teacher.TeacherStatus;

import lombok.Getter;

@Getter
public class TeacherResponse {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private LocalDate admissionDate;
    private TeacherStatus status;
    private BigDecimal salary;
    private String notes;

    public TeacherResponse(Teacher teacher) {
        this.id = teacher.getId();
        this.name = teacher.getName();
        this.email = teacher.getEmail();
        this.phone = teacher.getPhone();
        this.role = teacher.getRole();
        this.admissionDate = teacher.getAdmissionDate();
        this.status = teacher.getStatus();
        this.salary = teacher.getSalary();
        this.notes = teacher.getNotes();
    }
}
