package com.example.tech_go_api.services.profileplayer;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.users.Role;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.domain.users.profileadmin.ProfileAdmin;
import com.example.tech_go_api.dto.profileplayer.ProfilePlayerCreateRequestDTO;
import com.example.tech_go_api.dto.profileplayer.ProfilePlayerSoftDeleteRequestDTO;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.profileadmin.ProfileAdminRepository;
import com.example.tech_go_api.repositories.profileplayer.ProfilePlayerRepository;
import com.example.tech_go_api.repositories.school.SchoolRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfilePlayerService {
	
	private final ProfilePlayerRepository profilePlayerRepository;
	private final ProfileAdminRepository profileAdminRepository;
	private final SchoolRepository schoolRepository;

	 public ResponseEntity<String> createProfilePlayer(ProfilePlayerCreateRequestDTO dto, User user) {
		 
		 	ProfileAdmin profileAdmin = profileAdminRepository.findById(user.getId())
		 			.orElseThrow(()-> new NotFoundException("Usuário Admin não encontrado"));
		 	
		 	School school = profileAdmin.getSchool();
	         
	        ProfilePlayer profilePlayer = new ProfilePlayer();
	        profilePlayer.setEmail(null);
	        profilePlayer.setPassword(null);
	        profilePlayer.setRole(Role.PLAYER);
	        profilePlayer.setFirstname(dto.firstname());
	        profilePlayer.setLastname(dto.lastname());
	        profilePlayer.setSchool(school);
	        profilePlayer.setIsDeleted(false);

	        ProfilePlayer saved = profilePlayerRepository.save(profilePlayer);
	        return ResponseEntity.ok("Usuario criado com sucesso");
	    }
	 
	 
	 public List<ProfilePlayer> findAllBySchoolAndIsDeletFalse(User user){
		 ProfileAdmin profileAdmin = profileAdminRepository.findById(user.getId())
		 			.orElseThrow(()-> new NotFoundException("Usuário Admin não encontrado"));
		 	
		 School school = profileAdmin.getSchool();	 	
		 return profilePlayerRepository.findAllBySchoolAndIsDeletedFalse(school);
	 }
 
	 
	 public ResponseEntity<String> softDeleteProfilePlayer(ProfilePlayerSoftDeleteRequestDTO dto, User user){
		 ProfileAdmin profileAdmin = profileAdminRepository.findById(user.getId())
		 			.orElseThrow(()-> new NotFoundException("Usuário Admin não encontrado"));
		 School school = profileAdmin.getSchool();
		 
		 ProfilePlayer profilePlayer = profilePlayerRepository.findById(dto.id())
		            .orElseThrow(() -> new NotFoundException("Aluno não encontrado"));
		 
		 if (!profilePlayer.getSchool().getId().equals(school.getId())) {
		        throw new IllegalArgumentException("Este aluno não pertence à sua escola.");
		    }
		 
		 profilePlayer.setIsDeleted(true);
		 profilePlayerRepository.save(profilePlayer);	 
		 return ResponseEntity.ok("Usuario deletado com sucesso");
	 }
	 
}
