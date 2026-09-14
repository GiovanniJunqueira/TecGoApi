package com.example.tech_go_api.services.game;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tech_go_api.domain.game.Game;
import com.example.tech_go_api.domain.game.GameCategory;
import com.example.tech_go_api.domain.game.GamePlayerStats;
import com.example.tech_go_api.domain.game.GameType;
import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.example.tech_go_api.dto.game.GameAttendanceEntryDTO;
import com.example.tech_go_api.dto.game.GameCreateRequest;
import com.example.tech_go_api.dto.game.GamePlayerStatsRequest;
import com.example.tech_go_api.dto.game.GamePlayerStatsResponse;
import com.example.tech_go_api.dto.game.GameResponse;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.game.GamePlayerStatsRepository;
import com.example.tech_go_api.repositories.game.GameRepository;
import com.example.tech_go_api.repositories.profileplayer.ProfilePlayerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final GamePlayerStatsRepository gamePlayerStatsRepository;
    private final ProfilePlayerRepository profilePlayerRepository;

    public GameResponse create(GameCreateRequest request) {
        Game game = new Game();
        game.setType(request.type());
        game.setCategory(request.category());
        game.setOpponent(request.opponent());
        game.setDate(request.date());
        game.setHomeScore(request.homeScore());
        game.setAwayScore(request.awayScore());
        game.setLocation(request.location());

        Game savedGame = gameRepository.save(game);

        List<GamePlayerStats> stats = request.players() != null
                ? request.players().stream().map(p -> toStats(p, savedGame, null)).collect(Collectors.toList())
                : List.of();

        gamePlayerStatsRepository.saveAll(stats);

        List<GamePlayerStatsResponse> playerResponses = stats.stream()
                .map(this::toStatsResponse)
                .collect(Collectors.toList());

        return new GameResponse(savedGame, playerResponses);
    }

    @Transactional
    public GameResponse update(String id, GameCreateRequest request) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Jogo não encontrado"));

        game.setType(request.type());
        game.setCategory(request.category());
        game.setOpponent(request.opponent());
        game.setDate(request.date());
        game.setHomeScore(request.homeScore());
        game.setAwayScore(request.awayScore());
        game.setLocation(request.location());

        Map<String, Boolean> attendedByPlayerId = game.getPlayers().stream()
                .filter(s -> s.getPlayer() != null)
                .collect(Collectors.toMap(s -> s.getPlayer().getId(), GamePlayerStats::getAttended, (a, b) -> a));

        game.getPlayers().clear();
        if (request.players() != null) {
            request.players().stream()
                    .map(p -> toStats(p, game, attendedByPlayerId.get(p.playerId())))
                    .forEach(game.getPlayers()::add);
        }

        Game savedGame = gameRepository.save(game);

        List<GamePlayerStatsResponse> playerResponses = savedGame.getPlayers().stream()
                .map(this::toStatsResponse)
                .collect(Collectors.toList());

        return new GameResponse(savedGame, playerResponses);
    }

    private GamePlayerStats toStats(GamePlayerStatsRequest req, Game game, Boolean attended) {
        ProfilePlayer player = profilePlayerRepository.findById(req.playerId())
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado"));

        GamePlayerStats stats = new GamePlayerStats();
        stats.setGame(game);
        stats.setPlayer(player);
        stats.setGoals(req.goals());
        stats.setStarter(req.starter());
        stats.setNotes(req.notes());
        stats.setAttended(attended);
        return stats;
    }

    private GamePlayerStatsResponse toStatsResponse(GamePlayerStats stats) {
        String name = stats.getPlayer() != null
                ? stats.getPlayer().getFirstname() + " " + stats.getPlayer().getLastname()
                : null;
        return new GamePlayerStatsResponse(
                stats.getPlayer() != null ? stats.getPlayer().getId() : null,
                name,
                stats.getGoals(),
                stats.getStarter(),
                stats.getNotes(),
                stats.getAttended()
        );
    }

    @Transactional
    public GameResponse updateAttendance(String gameId, List<GameAttendanceEntryDTO> entries) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException("Jogo não encontrado"));

        Map<String, Boolean> attendedByPlayerId = entries.stream()
                .collect(Collectors.toMap(GameAttendanceEntryDTO::playerId, GameAttendanceEntryDTO::attended, (a, b) -> b));

        for (GamePlayerStats stats : game.getPlayers()) {
            if (stats.getPlayer() != null && attendedByPlayerId.containsKey(stats.getPlayer().getId())) {
                stats.setAttended(attendedByPlayerId.get(stats.getPlayer().getId()));
            }
        }

        Game saved = gameRepository.save(game);

        List<GamePlayerStatsResponse> playerResponses = saved.getPlayers().stream()
                .map(this::toStatsResponse)
                .collect(Collectors.toList());

        return new GameResponse(saved, playerResponses);
    }

    public List<GameResponse> findAll(GameType type, GameCategory category, LocalDate startDate, LocalDate endDate) {
        List<Game> games;

        if (type != null && category != null && startDate != null && endDate != null) {
            games = gameRepository.findByTypeAndCategoryAndDateBetween(type, category, startDate, endDate);
        } else if (type != null && category != null) {
            games = gameRepository.findByTypeAndCategory(type, category);
        } else if (type != null) {
            games = gameRepository.findByType(type);
        } else if (category != null) {
            games = gameRepository.findByCategory(category);
        } else if (startDate != null && endDate != null) {
            games = gameRepository.findByDateBetween(startDate, endDate);
        } else {
            games = gameRepository.findAll();
        }

        return games.stream()
                .map(g -> new GameResponse(g,
                        g.getPlayers() != null ? g.getPlayers().stream()
                                .map(this::toStatsResponse)
                                .collect(Collectors.toList()) : List.of()))
                .collect(Collectors.toList());
    }

    public GameResponse findById(String id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Jogo não encontrado"));

        List<GamePlayerStatsResponse> stats = game.getPlayers() != null
                ? game.getPlayers().stream().map(this::toStatsResponse).collect(Collectors.toList())
                : List.of();

        return new GameResponse(game, stats);
    }

    public List<GameResponse> findByPlayer(String playerId) {
        return gamePlayerStatsRepository.findByPlayerId(playerId).stream()
                .map(GamePlayerStats::getGame)
                .distinct()
                .map(g -> new GameResponse(g,
                        g.getPlayers() != null ? g.getPlayers().stream()
                                .map(this::toStatsResponse)
                                .collect(Collectors.toList()) : List.of()))
                .collect(Collectors.toList());
    }

    public void delete(String id) {
        gameRepository.deleteById(id);
    }
}
