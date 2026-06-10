package com.example.tech_go_api.services.responsible;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.example.tech_go_api.domain.responsible.Responsible;
import com.example.tech_go_api.dto.responsible.ResponsibleResponse;
import com.example.tech_go_api.dto.responsible.ResponsibleStudentSummary;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.profileplayer.ProfilePlayerRepository;
import com.example.tech_go_api.repositories.responsible.ResponsibleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResponsibleService {

    private final ResponsibleRepository responsibleRepository;
    private final ProfilePlayerRepository profilePlayerRepository;

    public List<ResponsibleResponse> findAll(String name, String studentName) {
        List<Responsible> responsibles;

        if (name != null && !name.isBlank()) {
            responsibles = responsibleRepository.findByNameContainingIgnoreCase(name);
        } else if (studentName != null && !studentName.isBlank()) {
            responsibles = responsibleRepository.findByStudentName(studentName);
        } else {
            responsibles = responsibleRepository.findAll();
        }

        return responsibles.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ResponsibleResponse findById(String id) {
        Responsible responsible = responsibleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Responsável não encontrado"));
        return toResponse(responsible);
    }

    public ResponsibleResponse create(ResponsibleResponse dto) {
        Responsible responsible = new Responsible();
        updateEntityFromDto(responsible, dto);
        Responsible saved = responsibleRepository.save(responsible);
        return toResponse(saved);
    }

    public ResponsibleResponse update(String id, ResponsibleResponse dto) {
        Responsible responsible = responsibleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Responsável não encontrado"));
        updateEntityFromDto(responsible, dto);
        Responsible saved = responsibleRepository.save(responsible);
        return toResponse(saved);
    }

    public void delete(String id) {
        responsibleRepository.deleteById(id);
    }

	public List<ResponsibleResponse> findByPlayer(String playerId) {
		return responsibleRepository.findAll().stream()
		    	.filter(r -> r.getPlayers().stream().anyMatch(p -> p.getId().equals(playerId)))
		    	.map(this::toResponse)
		    	.toList();
	}

	public void updateResponsiblesForPlayer(String playerId, List<String> responsibleIds) {
		ProfilePlayer player = profilePlayerRepository.findById(playerId)
		    	.orElseThrow(() -> new NotFoundException("Aluno não encontrado"));
		
		List<Responsible> all = responsibleRepository.findAll();
		for (Responsible r : all) {
			if (r.getPlayers().removeIf(p -> p.getId().equals(playerId))) {
				responsibleRepository.save(r);
			}
		}
		
		List<Responsible> newResponsibles = responsibleRepository.findAllById(responsibleIds);
		for (Responsible r : newResponsibles) {
			r.getPlayers().add(player);
			responsibleRepository.save(r);
		}
	}

    private void updateEntityFromDto(Responsible entity, ResponsibleResponse dto) {
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
