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
import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.staff.Permission;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.game.GameAttendanceEntryDTO;
import com.example.tech_go_api.dto.game.GameCreateRequest;
import com.example.tech_go_api.dto.game.GamePlayerStatsRequest;
import com.example.tech_go_api.dto.game.GamePlayerStatsResponse;
import com.example.tech_go_api.dto.game.GameResponse;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.game.GamePlayerStatsRepository;
import com.example.tech_go_api.repositories.game.GameRepository;
import com.example.tech_go_api.repositories.profileplayer.ProfilePlayerRepository;
import com.example.tech_go_api.services.school.SchoolResolverService;
import com.example.tech_go_api.services.staff.PermissionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final GamePlayerStatsRepository gamePlayerStatsRepository;
    private final ProfilePlayerRepository profilePlayerRepository;
    private final SchoolResolverService schoolResolverService;
    private final PermissionService permissionService;

    private School schoolOf(User user) {
        return schoolResolverService.schoolOf(user);
    }

    public GameResponse create(GameCreateRequest request, User user) {
        permissionService.requirePermission(user, Permission.JOGOS_CRIAR);
        Game game = new Game();
        game.setType(request.type());
        game.setCategory(request.category());
        game.setOpponent(request.opponent());
        game.setDate(request.date());
        game.setHomeScore(request.homeScore());
        game.setAwayScore(request.awayScore());
        game.setLocation(request.location());
        game.setSchool(schoolOf(user));

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
    public GameResponse update(String id, GameCreateRequest request, User user) {
        permissionService.requirePermission(user, Permission.JOGOS_EDITAR);
        Game game = findOwnedGame(id, user);

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
    public GameResponse updateAttendance(String gameId, List<GameAttendanceEntryDTO> entries, User user) {
        permissionService.requirePermission(user, Permission.JOGOS_FAZER_CHAMADA);
        Game game = findOwnedGame(gameId, user);

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

    public List<GameResponse> findAll(GameType type, GameCategory category, LocalDate startDate, LocalDate endDate, User user) {
        permissionService.requirePermission(user, Permission.JOGOS_VER);
        List<Game> games = gameRepository.search(schoolOf(user), type, category, startDate, endDate);

        return games.stream()
                .map(g -> new GameResponse(g,
                        g.getPlayers() != null ? g.getPlayers().stream()
                                .map(this::toStatsResponse)
                                .collect(Collectors.toList()) : List.of()))
                .collect(Collectors.toList());
    }

    public GameResponse findById(String id, User user) {
        permissionService.requirePermission(user, Permission.JOGOS_VER);
        Game game = findOwnedGame(id, user);

        List<GamePlayerStatsResponse> stats = game.getPlayers() != null
                ? game.getPlayers().stream().map(this::toStatsResponse).collect(Collectors.toList())
                : List.of();

        return new GameResponse(game, stats);
    }

    public List<GameResponse> findByPlayer(String playerId, User user) {
        permissionService.requirePermission(user, Permission.JOGOS_VER);
        School school = schoolOf(user);
        return gamePlayerStatsRepository.findByPlayerId(playerId).stream()
                .map(GamePlayerStats::getGame)
                .filter(g -> g.getSchool() != null && g.getSchool().getId().equals(school.getId()))
                .distinct()
                .map(g -> new GameResponse(g,
                        g.getPlayers() != null ? g.getPlayers().stream()
                                .map(this::toStatsResponse)
                                .collect(Collectors.toList()) : List.of()))
                .collect(Collectors.toList());
    }

    private Game findOwnedGame(String id, User user) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Jogo não encontrado"));

        if (game.getSchool() == null || !game.getSchool().getId().equals(schoolOf(user).getId())) {
            throw new IllegalArgumentException("Este jogo não pertence à sua escola.");
        }

        return game;
    }

    public void delete(String id, User user) {
        permissionService.requirePermission(user, Permission.JOGOS_EXCLUIR);
        findOwnedGame(id, user);
        gameRepository.deleteById(id);
    }
}
