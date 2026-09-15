package com.example.tech_go_api.services.aula;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.tech_go_api.domain.aula.AulaGrupo;
import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.domain.users.profileadmin.ProfileAdmin;
import com.example.tech_go_api.dto.aula.AulaGrupoCreateRequestDTO;
import com.example.tech_go_api.dto.aula.AulaGrupoResponseDTO;
import com.example.tech_go_api.dto.aula.AulaPlayerSummaryDTO;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.aula.AulaGrupoRepository;
import com.example.tech_go_api.repositories.aula.AulaSessaoRepository;
import com.example.tech_go_api.repositories.profileadmin.ProfileAdminRepository;
import com.example.tech_go_api.repositories.profileplayer.ProfilePlayerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AulaGrupoService {

    private final AulaGrupoRepository aulaGrupoRepository;
    private final AulaSessaoRepository aulaSessaoRepository;
    private final ProfileAdminRepository profileAdminRepository;
    private final ProfilePlayerRepository profilePlayerRepository;

    private School schoolOf(User user) {
        ProfileAdmin profileAdmin = profileAdminRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Usuário Admin não encontrado"));
        return profileAdmin.getSchool();
    }

    public AulaGrupoResponseDTO create(AulaGrupoCreateRequestDTO dto, User user) {
        AulaGrupo grupo = new AulaGrupo();
        grupo.setName(dto.name());
        grupo.setSchool(schoolOf(user));

        if (dto.playerIds() != null && !dto.playerIds().isEmpty()) {
            grupo.setPlayers(new java.util.HashSet<>(profilePlayerRepository.findAllById(dto.playerIds())));
        }

        AulaGrupo saved = aulaGrupoRepository.save(grupo);
        return toResponse(saved);
    }

    public List<AulaGrupoResponseDTO> findAll(User user) {
        return aulaGrupoRepository.findBySchool(schoolOf(user)).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AulaGrupoResponseDTO update(String id, AulaGrupoCreateRequestDTO dto, User user) {
        AulaGrupo grupo = findOwnedGrupo(id, user);

        grupo.setName(dto.name());
        grupo.setPlayers(dto.playerIds() != null
                ? new java.util.HashSet<>(profilePlayerRepository.findAllById(dto.playerIds()))
                : new java.util.HashSet<>());

        return toResponse(aulaGrupoRepository.save(grupo));
    }

    public void delete(String id, User user) {
        AulaGrupo grupo = findOwnedGrupo(id, user);
        aulaSessaoRepository.deleteAll(aulaSessaoRepository.findByGrupoOrderByDateDesc(grupo));
        aulaGrupoRepository.delete(grupo);
    }

    private AulaGrupo findOwnedGrupo(String id, User user) {
        AulaGrupo grupo = aulaGrupoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Grupo de aula não encontrado"));

        if (!grupo.getSchool().getId().equals(schoolOf(user).getId())) {
            throw new IllegalArgumentException("Este grupo não pertence à sua escola.");
        }

        return grupo;
    }

    private AulaGrupoResponseDTO toResponse(AulaGrupo grupo) {
        List<AulaPlayerSummaryDTO> players = grupo.getPlayers().stream()
                .map(p -> new AulaPlayerSummaryDTO(p.getId(), p.getFirstname(), p.getLastname()))
                .collect(Collectors.toList());
        return new AulaGrupoResponseDTO(grupo.getId(), grupo.getName(), players);
    }
}
