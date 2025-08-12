package com.example.tech_go_api.domain.users.profileadmin;

import java.io.Serializable;

import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.users.base.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
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
@Table(name = "t_profile_admin")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "user_id")
public class ProfileAdmin extends User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String phone;
    private String firstname;
    private String lastname;
    private String document;

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = true)
    @JsonBackReference
    private School school;
}
