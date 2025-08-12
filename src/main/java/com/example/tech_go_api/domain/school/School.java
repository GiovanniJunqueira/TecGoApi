package com.example.tech_go_api.domain.school;

import java.util.List;

import com.example.tech_go_api.domain.users.profileadmin.ProfileAdmin;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_school")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class School {

    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String cnpj;
    private String address;
    private String city;

    @OneToMany(mappedBy = "school")
    @JsonManagedReference
    private List<ProfileAdmin> profileAdmins;

    private String logoReferenceId;

}
