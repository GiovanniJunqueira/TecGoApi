package com.example.tech_go_api.domain.staff;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.users.base.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_staff_member")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "user_id")
public class StaffMember extends User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String firstname;
    private String lastname;

    @Enumerated(EnumType.STRING)
    private StaffRoleType staffRole;

    private String customRoleLabel;

    private String phone;
    private String document;
    private LocalDate admissionDate;
    private BigDecimal salary;
    private String notes;

    @Enumerated(EnumType.STRING)
    private StaffStatus status;

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = true)
    @JsonBackReference
    private School school;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "t_staff_permission", joinColumns = @JoinColumn(name = "staff_id"))
    @Column(name = "permission")
    @Enumerated(EnumType.STRING)
    private Set<Permission> permissions = new HashSet<>();
}
