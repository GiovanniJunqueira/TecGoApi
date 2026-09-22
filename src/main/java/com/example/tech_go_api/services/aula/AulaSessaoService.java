package com.example.tech_go_api.services.aula;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tech_go_api.domain.aula.AulaGrupo;
import com.example.tech_go_api.domain.aula.AulaPresenca;
import com.example.tech_go_api.domain.aula.AulaSessao;
import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.staff.Permission;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.aula.AulaPresencaEntryDTO;
import com.example.tech_go_api.dto.aula.AulaPresencaResponseDTO;
import com.example.tech_go_api.dto.aula.AulaSessaoCreateRequestDTO;
import com.example.tech_go_api.dto.aula.AulaSessaoResponseDTO;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.aula.AulaGrupoRepository;
import com.example.tech_go_api.repositories.aula.AulaSessaoRepository;
import com.example.tech_go_api.services.school.SchoolResolverService;
import com.example.tech_go_api.services.staff.PermissionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AulaSessaoService {

    private final AulaSessaoRepository aulaSessaoRepository;
    private final AulaGrupoRepository aulaGrupoRepository;
    private final SchoolResolverService schoolResolverService;
    private final PermissionService permissionService;

    private School schoolOf(User user) {
        return schoolResolverService.schoolOf(user);
    }

    @Transactional
    public AulaSessaoResponseDTO create(String grupoId, AulaSessaoCreateRequestDTO dto, User user) {
        permissionService.requirePermission(user, Permission.AULAS_REGISTRAR_AULA);
        AulaGrupo grupo = findOwnedGrupo(grupoId, user);

        AulaSessao sessao = new AulaSessao();
        sessao.setGrupo(grupo);
        sessao.setDate(dto.date());

        for (ProfilePlayer player : grupo.getPlayers()) {
            AulaPresenca presenca = new AulaPresenca();
            presenca.setSessao(sessao);
            presenca.setPlayer(player);
            presenca.setPresent(null);
            sessao.getPresencas().add(presenca);
        }

        return toResponse(aulaSessaoRepository.save(sessao));
    }

    public List<AulaSessaoResponseDTO> findByGrupo(String grupoId, User user) {
        permissionService.requirePermission(user, Permission.AULAS_VER_GRUPOS);
        AulaGrupo grupo = findOwnedGrupo(grupoId, user);
        return aulaSessaoRepository.findByGrupoOrderByDateDesc(grupo).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AulaSessaoResponseDTO findById(String sessaoId, User user) {
        permissionService.requirePermission(user, Permission.AULAS_VER_GRUPOS);
        AulaSessao sessao = findOwnedSessao(sessaoId, user);
        return toResponse(sessao);
    }

    @Transactional
    public AulaSessaoResponseDTO updatePresenca(String sessaoId, List<AulaPresencaEntryDTO> entries, User user) {
        AulaSessao sessao = findOwnedSessao(sessaoId, user);

        Permission required = sessao.getDate() != null && sessao.getDate().isBefore(LocalDate.now())
                ? Permission.AULAS_EDITAR_CHAMADA_PASSADA
                : Permission.AULAS_FAZER_CHAMADA;
        permissionService.requirePermission(user, required);

        Map<String, Boolean> presentByPlayerId = entries.stream()
                .collect(Collectors.toMap(AulaPresencaEntryDTO::playerId, AulaPresencaEntryDTO::present, (a, b) -> b));

        for (AulaPresenca presenca : sessao.getPresencas()) {
            if (presenca.getPlayer() != null && presentByPlayerId.containsKey(presenca.getPlayer().getId())) {
                presenca.setPresent(presentByPlayerId.get(presenca.getPlayer().getId()));
            }
        }

        return toResponse(aulaSessaoRepository.save(sessao));
    }

    public void delete(String sessaoId, User user) {
        permissionService.requirePermission(user, Permission.AULAS_EXCLUIR_SESSAO);
        AulaSessao sessao = findOwnedSessao(sessaoId, user);
        aulaSessaoRepository.delete(sessao);
    }

    private AulaGrupo findOwnedGrupo(String grupoId, User user) {
        AulaGrupo grupo = aulaGrupoRepository.findById(grupoId)
                .orElseThrow(() -> new NotFoundException("Grupo de aula não encontrado"));

        if (!grupo.getSchool().getId().equals(schoolOf(user).getId())) {
            throw new IllegalArgumentException("Este grupo não pertence à sua escola.");
        }

        return grupo;
    }

    private AulaSessao findOwnedSessao(String sessaoId, User user) {
        AulaSessao sessao = aulaSessaoRepository.findById(sessaoId)
                .orElseThrow(() -> new NotFoundException("Aula não encontrada"));

        if (!sessao.getGrupo().getSchool().getId().equals(schoolOf(user).getId())) {
            throw new IllegalArgumentException("Esta aula não pertence à sua escola.");
        }

        return sessao;
    }

    private AulaSessaoResponseDTO toResponse(AulaSessao sessao) {
        List<AulaPresencaResponseDTO> presencas = sessao.getPresencas().stream()
                .map(p -> new AulaPresencaResponseDTO(
                        p.getPlayer() != null ? p.getPlayer().getId() : null,
                        p.getPlayer() != null ? p.getPlayer().getFirstname() + " " + p.getPlayer().getLastname() : null,
                        p.getPresent()
                ))
                .collect(Collectors.toList());

        return new AulaSessaoResponseDTO(
                sessao.getId(),
                sessao.getGrupo().getId(),
                sessao.getGrupo().getName(),
                sessao.getDate(),
                presencas
        );
    }
}
