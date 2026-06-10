package com.example.tech_go_api.domain.responsible;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_responsible")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Responsible implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String phone;

    private String email;

    private String document; // CPF ou RG

    private String address;
    private String addressNumber;
    private String addressNeighborhood;
    private String addressComplement;
    private String postcode;

    @ManyToMany
    @JoinTable(
            name = "t_player_responsible",
            joinColumns = @JoinColumn(name = "responsible_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private Set<ProfilePlayer> players = new HashSet<>();
}
