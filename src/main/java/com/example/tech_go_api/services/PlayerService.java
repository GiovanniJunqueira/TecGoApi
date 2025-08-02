package com.example.tech_go_api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.tech_go_api.dto.PlayerDTO;
import com.example.tech_go_api.dto.PlayerFilter;
import com.example.tech_go_api.dto.PlayerResponse;
import com.example.tech_go_api.model.Player;
import com.example.tech_go_api.repositories.PlayerRepository;
import com.example.tech_go_api.specification.PlayerSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlayerService {

	@Autowired
	private PlayerRepository playerRepository;
	
	public ResponseEntity<?> register(PlayerDTO dto){
		Player player = new Player();
		player.setName(dto.name());
		player.setBirthDate(dto.birthDate());
		player.setRg(dto.rg());
		player.setCpf(dto.cpf());
		player.setPhone(dto.phone());
		player.setAddress(dto.address());
		player.setSchoolName(dto.schoolName());
		player.setSchoolName(dto.schoolName());
		player.setGrade(dto.grade());
		player.setSchoolSchedule(dto.schoolSchedule());
		player.setInstagram(dto.instagram());
		player.setFacebook(dto.facebook());
		playerRepository.save(player);
		return ResponseEntity.ok().build();
	}
	
	public List<PlayerResponse> searchPlayers(PlayerFilter filter, Pageable pageable) {
        log.debug("Buscando players com filtros: {}", filter);

        var retPlayer = playerRepository.findAll(
            Specification.<Player>where(PlayerSpecification.hasName(filter.getName()))
                .and(PlayerSpecification.birthDateBetween(filter.getBirthDateFrom(), filter.getBirthDateTo()))
                .and(PlayerSpecification.hasRg(filter.getRg()))
                .and(PlayerSpecification.hasCpf(filter.getCpf()))
                .and(PlayerSpecification.hasPhone(filter.getPhone()))
                .and(PlayerSpecification.hasAddress(filter.getAddress()))
                .and(PlayerSpecification.hasSchoolName(filter.getSchoolName()))
                .and(PlayerSpecification.hasGrade(filter.getGrade()))
                .and(PlayerSpecification.hasSchoolSchedule(filter.getSchoolSchedule()))
                .and(PlayerSpecification.hasInstagram(filter.getInstagram()))
                .and(PlayerSpecification.hasFacebook(filter.getFacebook())),
            pageable
        );
        return retPlayer.map(player -> new PlayerResponse(
                player.getUuid(),
                player.getName(),
                player.getBirthDate(),
                player.getRg(),
                player.getCpf(),
                player.getPhone(),
                player.getAddress(),
                player.getSchoolName(),
                player.getGrade(),
                player.getSchoolSchedule(),
                player.getInstagram(),
                player.getFacebook()
            )).getContent();
    }	
}
