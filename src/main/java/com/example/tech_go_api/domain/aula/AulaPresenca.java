package com.example.tech_go_api.domain.aula;

import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_aula_presenca")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AulaPresenca {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "sessao_id")
    private AulaSessao sessao;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private ProfilePlayer player;

    private Boolean present;
}
