package com.example.tech_go_api.services.profileplayer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

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
import com.example.tech_go_api.services.payment.PaymentService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfilePlayerService {
	
	private final ProfilePlayerRepository profilePlayerRepository;
	private final ProfileAdminRepository profileAdminRepository;
	private final SchoolRepository schoolRepository;
	
	@Autowired
	PaymentService paymentService;

	 public String createProfilePlayer(ProfilePlayerCreateRequestDTO dto, User user) {
		 
		 	ProfileAdmin profileAdmin = profileAdminRepository.findById(user.getId())
		 			.orElseThrow(()-> new NotFoundException("Usuário Admin não encontrado"));
		 	
		 	School school = profileAdmin.getSchool();
	         
	        ProfilePlayer profilePlayer = new ProfilePlayer();
	        profilePlayer.setRole(Role.PLAYER);
	        profilePlayer.setFirstname(dto.firstname());
	        profilePlayer.setLastname(dto.lastname());
	        profilePlayer.setSchool(school);
	        profilePlayer.setBirthDate(dto.birthDate());
	        profilePlayer.setRg(dto.rg());
	        profilePlayer.setCpf(dto.cpf());
	        profilePlayer.setPhoneNumber(dto.phoneNumber());
	        profilePlayer.setAddress(dto.address());
	        profilePlayer.setAddressNumber(dto.addressNumber());
	        profilePlayer.setAddressNeighborhood(dto.addressNeighborhood());
	        profilePlayer.setAddressComplement(dto.addressComplement());
	        profilePlayer.setPostcode(dto.postcode());
	        profilePlayer.setCollege(dto.college());
	        profilePlayer.setCollegeAddress(dto.collegeAddress());
	        profilePlayer.setCollegeNeighborhood(dto.collegeNeighborhood());
	        profilePlayer.setCollegeComplement(dto.collegeComplement());
	        profilePlayer.setCollegePostcode(dto.collegePostcode());
	        profilePlayer.setCollegePhone(dto.collegePhone());
	        profilePlayer.setCollegeSeries(dto.collegeSeries());
	        profilePlayer.setCollegeTime(dto.collegeTime());
	        profilePlayer.setOrigin(dto.origin());
	        profilePlayer.setRegistrationId(dto.registrationId());
	        profilePlayer.setIsDeleted(false);

	        ProfilePlayer saved = profilePlayerRepository.save(profilePlayer);
	        
	        String currentMonth = LocalDate.now().getYear() + "-" + LocalDate.now().getMonthValue();
	        paymentService.createPayment(saved, currentMonth);
	        
	        return (saved.getId());
	    }
	 
//	 @Cacheable(cacheNames = "getAllPlayers", key = "#schoolId")
	 public Page<ProfilePlayer> findAllBySchoolAndIsDeletFalse(User user, Pageable pageable){
		 ProfileAdmin profileAdmin = profileAdminRepository.findById(user.getId())
		 			.orElseThrow(()-> new NotFoundException("Usuário Admin não encontrado"));
		 	
		 School school = profileAdmin.getSchool();	 	
		 return profilePlayerRepository.findAllBySchoolAndIsDeletedFalse(school, pageable);
	 }
 
	 
	 public ResponseEntity<String> softDeleteProfilePlayer(String id, User user){
		 ProfileAdmin profileAdmin = profileAdminRepository.findById(user.getId())
		 			.orElseThrow(()-> new NotFoundException("Usuário Admin não encontrado"));
		 School school = profileAdmin.getSchool();
		 
		 ProfilePlayer profilePlayer = profilePlayerRepository.findById(id)
		            .orElseThrow(() -> new NotFoundException("Aluno não encontrado"));
		 
		 if (!profilePlayer.getSchool().getId().equals(school.getId())) {
		        throw new IllegalArgumentException("Este aluno não pertence à sua escola.");
		    }
		 
		 profilePlayer.setIsDeleted(true);
		 profilePlayerRepository.save(profilePlayer);	 
		 return ResponseEntity.ok("Usuario deletado com sucesso");
	 }
	 
	 public ProfilePlayer getById(String id, User user){
		 ProfileAdmin profileAdmin = profileAdminRepository.findById(user.getId())
		 			.orElseThrow(()-> new NotFoundException("Usuário Admin não encontrado"));
		 School school = profileAdmin.getSchool();
		 
		 ProfilePlayer profilePlayer = profilePlayerRepository.findById(id)
		            .orElseThrow(() -> new NotFoundException("Aluno não encontrado"));
		 
		 if (!profilePlayer.getSchool().getId().equals(school.getId())) {
		        throw new IllegalArgumentException("Este aluno não pertence à sua escola.");
		    }
		 	 
		 return profilePlayer;
	 }
	 
	 public ProfilePlayer updateProfilePlayer(String id, ProfilePlayerCreateRequestDTO dto, User user) {
	 	 ProfileAdmin profileAdmin = profileAdminRepository.findById(user.getId())
	 	     	.orElseThrow(() -> new NotFoundException("Usuário Admin não encontrado"));
	 	 School school = profileAdmin.getSchool();
	 	
	 	 ProfilePlayer profilePlayer = profilePlayerRepository.findById(id)
	 	     	.orElseThrow(() -> new NotFoundException("Aluno não encontrado"));
	 	
	 	 if (!profilePlayer.getSchool().getId().equals(school.getId())) {
	 	     throw new IllegalArgumentException("Este aluno não pertence à sua escola.");
	 	 }
	 	
	 	 profilePlayer.setFirstname(dto.firstname());
	 	 profilePlayer.setLastname(dto.lastname());
	 	 profilePlayer.setBirthDate(dto.birthDate());
	 	 profilePlayer.setRg(dto.rg());
	 	 profilePlayer.setCpf(dto.cpf());
	 	 profilePlayer.setPhoneNumber(dto.phoneNumber());
	 	 profilePlayer.setAddress(dto.address());
	 	 profilePlayer.setAddressNumber(dto.addressNumber());
	 	 profilePlayer.setAddressNeighborhood(dto.addressNeighborhood());
	 	 profilePlayer.setAddressComplement(dto.addressComplement());
	 	 profilePlayer.setPostcode(dto.postcode());
	 	 profilePlayer.setCollege(dto.college());
	 	 profilePlayer.setCollegeAddress(dto.collegeAddress());
	 	 profilePlayer.setCollegeNeighborhood(dto.collegeNeighborhood());
	 	 profilePlayer.setCollegeComplement(dto.collegeComplement());
	 	 profilePlayer.setCollegePostcode(dto.collegePostcode());
	 	 profilePlayer.setCollegePhone(dto.collegePhone());
	 	 profilePlayer.setCollegeSeries(dto.collegeSeries());
	 	 profilePlayer.setCollegeTime(dto.collegeTime());
	 	 profilePlayer.setOrigin(dto.origin());
	 	 profilePlayer.setRegistrationId(dto.registrationId());
	 	
	 	 return profilePlayerRepository.save(profilePlayer);
	 }
	 
}
