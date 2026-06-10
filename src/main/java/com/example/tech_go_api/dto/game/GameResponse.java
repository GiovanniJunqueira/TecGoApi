package com.example.tech_go_api.dto.game;

import java.time.LocalDate;
import java.util.List;

import com.example.tech_go_api.domain.game.Game;
import com.example.tech_go_api.domain.game.GameCategory;
import com.example.tech_go_api.domain.game.GameType;

public class GameResponse {

    private String id;
    private GameType type;
    private GameCategory category;
    private String opponent;
    private LocalDate date;
    private Integer homeScore;
    private Integer awayScore;
    private String location;
    private List<GamePlayerStatsResponse> players;

    public GameResponse(Game game, List<GamePlayerStatsResponse> players) {
        this.id = game.getId();
        this.type = game.getType();
        this.category = game.getCategory();
        this.opponent = game.getOpponent();
        this.date = game.getDate();
        this.homeScore = game.getHomeScore();
        this.awayScore = game.getAwayScore();
        this.location = game.getLocation();
        this.players = players;
    }

    public String getId() { return id; }
    public GameType getType() { return type; }
    public GameCategory getCategory() { return category; }
    public String getOpponent() { return opponent; }
    public LocalDate getDate() { return date; }
    public Integer getHomeScore() { return homeScore; }
    public Integer getAwayScore() { return awayScore; }
    public String getLocation() { return location; }
    public List<GamePlayerStatsResponse> getPlayers() { return players; }
}
