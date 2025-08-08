package com.example.tech_go_api.domain.school;

import com.example.tech_go_api.domain.users.profileadmin.ProfileAdmin;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "t_school")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class School implements Serializable {

    private static final long serialVersionUID = 1L;
    
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

}
