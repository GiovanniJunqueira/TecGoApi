package com.example.tech_go_api.domain.teacher;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_teacher")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Teacher implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String email;

    private String phone;

    private String role; // função/cargo

    private LocalDate admissionDate;

    @Enumerated(EnumType.STRING)
    private TeacherStatus status;

    private BigDecimal salary;

    private String notes;
}
