package com.example.tech_go_api.domain.game;

import java.io.Serializable;

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
@Table(name = "t_game_player_stats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GamePlayerStats implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private ProfilePlayer player;

    private Integer goals;

    private Boolean starter;

    private String notes;
}
