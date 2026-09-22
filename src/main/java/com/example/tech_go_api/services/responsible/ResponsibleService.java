package com.example.tech_go_api.services.responsible;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.example.tech_go_api.domain.responsible.Responsible;
import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.staff.Permission;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.responsible.ResponsibleResponse;
import com.example.tech_go_api.dto.responsible.ResponsibleStudentSummary;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.profileplayer.ProfilePlayerRepository;
import com.example.tech_go_api.repositories.responsible.ResponsibleRepository;
import com.example.tech_go_api.services.school.SchoolResolverService;
import com.example.tech_go_api.services.staff.PermissionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResponsibleService {

    private final ResponsibleRepository responsibleRepository;
    private final ProfilePlayerRepository profilePlayerRepository;
    private final SchoolResolverService schoolResolverService;
    private final PermissionService permissionService;

    public List<ResponsibleResponse> findAll(User user, String name, String studentName) {
        permissionService.requirePermission(user, Permission.RESPONSAVEIS_VER);
        School school = schoolResolverService.schoolOf(user);

        List<Responsible> responsibles;
        if (name != null && !name.isBlank()) {
            responsibles = responsibleRepository.findBySchoolAndNameContainingIgnoreCase(school, name);
        } else if (studentName != null && !studentName.isBlank()) {
            responsibles = responsibleRepository.findBySchoolAndStudentName(school, studentName);
        } else {
            responsibles = responsibleRepository.findBySchool(school);
        }

        return responsibles.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ResponsibleResponse findById(String id, User user) {
        permissionService.requirePermission(user, Permission.RESPONSAVEIS_VER);
        Responsible responsible = findOwnedResponsible(id, user);
        return toResponse(responsible);
    }

    public ResponsibleResponse create(ResponsibleResponse dto, User user) {
        permissionService.requirePermission(user, Permission.RESPONSAVEIS_CRIAR);
        School school = schoolResolverService.schoolOf(user);

        Responsible responsible = new Responsible();
        responsible.setSchool(school);
        updateEntityFromDto(responsible, dto, school);
        Responsible saved = responsibleRepository.save(responsible);
        return toResponse(saved);
    }

    public ResponsibleResponse update(String id, ResponsibleResponse dto, User user) {
        permissionService.requirePermission(user, Permission.RESPONSAVEIS_EDITAR);
        Responsible responsible = findOwnedResponsible(id, user);
        updateEntityFromDto(responsible, dto, responsible.getSchool());
        Responsible saved = responsibleRepository.save(responsible);
        return toResponse(saved);
    }

    public void delete(String id, User user) {
        permissionService.requirePermission(user, Permission.RESPONSAVEIS_EXCLUIR);
        Responsible responsible = findOwnedResponsible(id, user);
        responsibleRepository.delete(responsible);
    }

	public List<ResponsibleResponse> findByPlayer(String playerId, User user) {
		permissionService.requirePermission(user, Permission.RESPONSAVEIS_VER);
		School school = schoolResolverService.schoolOf(user);

		return responsibleRepository.findByPlayers_Id(playerId).stream()
				.filter(r -> r.getSchool() != null && r.getSchool().getId().equals(school.getId()))
				.map(this::toResponse)
				.toList();
	}

	public void updateResponsiblesForPlayer(String playerId, List<String> responsibleIds, User user) {
		permissionService.requirePermission(user, Permission.RESPONSAVEIS_EDITAR);
		School school = schoolResolverService.schoolOf(user);

		ProfilePlayer player = profilePlayerRepository.findById(playerId)
		    	.orElseThrow(() -> new NotFoundException("Aluno não encontrado"));

		if (!player.getSchool().getId().equals(school.getId())) {
			throw new IllegalArgumentException("Este aluno não pertence à sua escola.");
		}

		List<Responsible> currentlyLinked = responsibleRepository.findByPlayers_Id(playerId);
		for (Responsible r : currentlyLinked) {
			if (r.getPlayers().removeIf(p -> p.getId().equals(playerId))) {
				responsibleRepository.save(r);
			}
		}

		List<Responsible> newResponsibles = responsibleRepository.findAllById(responsibleIds);
		for (Responsible r : newResponsibles) {
			if (r.getSchool() == null || !r.getSchool().getId().equals(school.getId())) {
				throw new IllegalArgumentException("Um dos responsáveis não pertence à sua escola.");
			}
			r.getPlayers().add(player);
			responsibleRepository.save(r);
		}
	}

	private Responsible findOwnedResponsible(String id, User user) {
		School school = schoolResolverService.schoolOf(user);
		Responsible responsible = responsibleRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Responsável não encontrado"));

		if (responsible.getSchool() == null || !responsible.getSchool().getId().equals(school.getId())) {
			throw new IllegalArgumentException("Este responsável não pertence à sua escola.");
		}
		return responsible;
	}

    private void updateEntityFromDto(Responsible entity, ResponsibleResponse dto, School school) {
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setDocument(dto.getDocument());
        entity.setAddress(dto.getAddress());
        entity.setAddressNumber(dto.getAddressNumber());
        entity.setAddressNeighborhood(dto.getAddressNeighborhood());
        entity.setAddressComplement(dto.getAddressComplement());
        entity.setPostcode(dto.getPostcode());

        if (dto.getStudents() != null) {
            List<String> playerIds = dto.getStudents().stream()
                    .map(ResponsibleStudentSummary::id)
                    .toList();
            List<ProfilePlayer> players = profilePlayerRepository.findAllById(playerIds);
            for (ProfilePlayer p : players) {
                if (!p.getSchool().getId().equals(school.getId())) {
                    throw new IllegalArgumentException("Um dos alunos não pertence à sua escola.");
                }
            }
            entity.getPlayers().clear();
            entity.getPlayers().addAll(players);
        }
    }

    private ResponsibleResponse toResponse(Responsible entity) {
        List<ResponsibleStudentSummary> students = entity.getPlayers() != null
                ? entity.getPlayers().stream()
                .map(p -> new ResponsibleStudentSummary(
                        p.getId(),
                        p.getFirstname(),
                        p.getLastname(),
                        p.getRegistrationId()
                ))
                .collect(Collectors.toList())
                : List.of();

        return new ResponsibleResponse(
                entity.getId(),
                entity.getName(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getDocument(),
                entity.getAddress(),
                entity.getAddressNumber(),
                entity.getAddressNeighborhood(),
                entity.getAddressComplement(),
                entity.getPostcode(),
                students
        );
    }
}
