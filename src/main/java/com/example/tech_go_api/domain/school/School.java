package com.example.tech_go_api.domain.school;

import com.example.tech_go_api.domain.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    private List<User> users;
}
