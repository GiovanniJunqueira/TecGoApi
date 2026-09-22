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

import com.example.tech_go_api.domain.aula.AulaGrupo;
import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.example.tech_go_api.domain.responsible.Responsible;
import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.staff.Permission;
import com.example.tech_go_api.domain.users.Role;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.profileplayer.ProfilePlayerCreateRequestDTO;
import com.example.tech_go_api.dto.profileplayer.ProfilePlayerSoftDeleteRequestDTO;
import com.example.tech_go_api.exceptions.BusinessException;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.aula.AulaGrupoRepository;
import com.example.tech_go_api.repositories.aula.AulaPresencaRepository;
import com.example.tech_go_api.repositories.game.GamePlayerStatsRepository;
import com.example.tech_go_api.repositories.payment.PaymentRepository;
import com.example.tech_go_api.repositories.profileplayer.ProfilePlayerRepository;
import com.example.tech_go_api.repositories.responsible.ResponsibleRepository;
import com.example.tech_go_api.repositories.school.SchoolRepository;
import com.example.tech_go_api.services.payment.PaymentService;
import com.example.tech_go_api.services.school.SchoolResolverService;
import com.example.tech_go_api.services.staff.PermissionService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfilePlayerService {

	private final ProfilePlayerRepository profilePlayerRepository;
	private final SchoolRepository schoolRepository;
	private final PaymentRepository paymentRepository;
	private final GamePlayerStatsRepository gamePlayerStatsRepository;
	private final ResponsibleRepository responsibleRepository;
	private final AulaGrupoRepository aulaGrupoRepository;
	private final AulaPresencaRepository aulaPresencaRepository;
	private final SchoolResolverService schoolResolverService;
	private final PermissionService permissionService;

	@Autowired
	PaymentService paymentService;

	 public String createProfilePlayer(ProfilePlayerCreateRequestDTO dto, User user) {
		 permissionService.requirePermission(user, Permission.ATLETAS_MATRICULAR);
		 School school = schoolResolverService.schoolOf(user);

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
	        profilePlayer.setTurma(resolveTurma(dto.turma(), dto.birthDate()));
	        profilePlayer.setPaymentPlan(dto.paymentPlan());
	        profilePlayer.setIsDeleted(false);

	        ProfilePlayer saved = profilePlayerRepository.save(profilePlayer);

	        assignAulaGrupo(saved, dto.aulaGrupoId(), school);

	        String currentMonth = String.format("%d-%02d", LocalDate.now().getYear(), LocalDate.now().getMonthValue());
	        paymentService.createPayment(saved, currentMonth);

	        return (saved.getId());
	    }

		 private void assignAulaGrupo(ProfilePlayer player, String aulaGrupoId, School school) {
			 for (AulaGrupo grupo : aulaGrupoRepository.findByPlayers_Id(player.getId())) {
				 if (grupo.getPlayers().removeIf(p -> p.getId().equals(player.getId()))) {
					 aulaGrupoRepository.save(grupo);
				 }
			 }

			 if (aulaGrupoId == null || aulaGrupoId.isBlank()) {
				 return;
			 }

			 AulaGrupo grupo = aulaGrupoRepository.findById(aulaGrupoId)
					 .orElseThrow(() -> new NotFoundException("Grupo de aula não encontrado"));

			 if (!grupo.getSchool().getId().equals(school.getId())) {
				 throw new IllegalArgumentException("Este grupo não pertence à sua escola.");
			 }

			 grupo.getPlayers().add(player);
			 aulaGrupoRepository.save(grupo);
		 }

//	 @Cacheable(cacheNames = "getAllPlayers", key = "#schoolId")
	 public Page<ProfilePlayer> findAllBySchoolAndIsDeletFalse(User user, String search, String turma, Pageable pageable){
		 permissionService.requirePermission(user, Permission.ATLETAS_VER);
		 School school = schoolResolverService.schoolOf(user);
		 return profilePlayerRepository.searchBySchoolAndIsDeleted(school, false, search, turma, pageable);
	 }


	 public ResponseEntity<String> softDeleteProfilePlayer(String id, User user){
		 permissionService.requirePermission(user, Permission.ATLETAS_INATIVAR);
		 School school = schoolResolverService.schoolOf(user);

		 ProfilePlayer profilePlayer = profilePlayerRepository.findById(id)
		            .orElseThrow(() -> new NotFoundException("Aluno não encontrado"));

		 if (!profilePlayer.getSchool().getId().equals(school.getId())) {
		        throw new IllegalArgumentException("Este aluno não pertence à sua escola.");
		    }

		 profilePlayer.setIsDeleted(true);
		 profilePlayer.setInactiveSince(LocalDate.now());
		 profilePlayerRepository.save(profilePlayer);
		 return ResponseEntity.ok("Aluno marcado como inativo com sucesso");
	 }

	 public ProfilePlayer reactivateProfilePlayer(String id, User user) {
		 permissionService.requirePermission(user, Permission.ATLETAS_REATIVAR);
		 School school = schoolResolverService.schoolOf(user);

		 ProfilePlayer profilePlayer = profilePlayerRepository.findById(id)
		            .orElseThrow(() -> new NotFoundException("Aluno não encontrado"));

		 if (!profilePlayer.getSchool().getId().equals(school.getId())) {
		        throw new IllegalArgumentException("Este aluno não pertence à sua escola.");
		    }

		 profilePlayer.setIsDeleted(false);
		 profilePlayer.setInactiveSince(null);
		 return profilePlayerRepository.save(profilePlayer);
	 }

	 public Page<ProfilePlayer> findAllInactiveBySchool(User user, String search, String turma, Pageable pageable) {
		 permissionService.requirePermission(user, Permission.ATLETAS_VER);
		 School school = schoolResolverService.schoolOf(user);
		 return profilePlayerRepository.searchBySchoolAndIsDeleted(school, true, search, turma, pageable);
	 }

	 @Transactional
	 public void hardDeleteProfilePlayer(String id, User user) {
		 permissionService.requirePermission(user, Permission.ATLETAS_EXCLUIR_PERMANENTE);
		 School school = schoolResolverService.schoolOf(user);

		 ProfilePlayer profilePlayer = profilePlayerRepository.findById(id)
		            .orElseThrow(() -> new NotFoundException("Aluno não encontrado"));

		 if (!profilePlayer.getSchool().getId().equals(school.getId())) {
		        throw new IllegalArgumentException("Este aluno não pertence à sua escola.");
		    }

		 if (Boolean.FALSE.equals(profilePlayer.getIsDeleted())) {
			 throw new BusinessException("Só é possível excluir permanentemente alunos que já estão inativos");
		 }

		 purgePlayer(profilePlayer);
	 }

	 @Transactional
	 public void purgePlayer(ProfilePlayer profilePlayer) {
		 String id = profilePlayer.getId();

		 paymentRepository.deleteAll(paymentRepository.findByProfilePlayerId(id));
		 gamePlayerStatsRepository.deleteAll(gamePlayerStatsRepository.findByPlayerId(id));
		 aulaPresencaRepository.deleteAll(aulaPresencaRepository.findByPlayer_Id(id));

		 for (Responsible responsible : responsibleRepository.findAll()) {
			 if (responsible.getPlayers().removeIf(p -> p.getId().equals(id))) {
				 responsibleRepository.save(responsible);
			 }
		 }

		 for (AulaGrupo grupo : aulaGrupoRepository.findByPlayers_Id(id)) {
			 if (grupo.getPlayers().removeIf(p -> p.getId().equals(id))) {
				 aulaGrupoRepository.save(grupo);
			 }
		 }

		 profilePlayerRepository.delete(profilePlayer);
	 }

	 public ProfilePlayer getById(String id, User user){
		 permissionService.requirePermission(user, Permission.ATLETAS_VER);
		 School school = schoolResolverService.schoolOf(user);

		 ProfilePlayer profilePlayer = profilePlayerRepository.findById(id)
		            .orElseThrow(() -> new NotFoundException("Aluno não encontrado"));

		 if (!profilePlayer.getSchool().getId().equals(school.getId())) {
		        throw new IllegalArgumentException("Este aluno não pertence à sua escola.");
		    }

		 return profilePlayer;
	 }

	 public ProfilePlayer updateProfilePlayer(String id, ProfilePlayerCreateRequestDTO dto, User user) {
	 	 permissionService.requirePermission(user, Permission.ATLETAS_EDITAR);
	 	 School school = schoolResolverService.schoolOf(user);

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
	 	 profilePlayer.setTurma(resolveTurma(dto.turma(), dto.birthDate()));
	 	 profilePlayer.setPaymentPlan(dto.paymentPlan());

	 	 ProfilePlayer saved = profilePlayerRepository.save(profilePlayer);

	 	 assignAulaGrupo(saved, dto.aulaGrupoId(), school);

	 	 return saved;
	 }

	 public List<com.example.tech_go_api.dto.profileplayer.PlayerReportResponseDTO> generateReport(
			 User user, String status, String turma, String aulaGrupoId) {
		 permissionService.requirePermission(user, Permission.ATLETAS_IMPRIMIR);
		 School school = schoolResolverService.schoolOf(user);

		 Boolean isDeleted;
		 if ("ATIVOS".equalsIgnoreCase(status)) {
			 isDeleted = false;
		 } else if ("INATIVOS".equalsIgnoreCase(status)) {
			 isDeleted = true;
		 } else {
			 isDeleted = null;
		 }

		 List<ProfilePlayer> players = profilePlayerRepository.findForReport(school, isDeleted, turma);

		 if (aulaGrupoId != null && !aulaGrupoId.isBlank()) {
			 AulaGrupo grupo = aulaGrupoRepository.findById(aulaGrupoId)
					 .orElseThrow(() -> new NotFoundException("Grupo de aula não encontrado"));
			 java.util.Set<String> allowedIds = grupo.getPlayers().stream()
					 .map(ProfilePlayer::getId)
					 .collect(java.util.stream.Collectors.toSet());
			 players = players.stream().filter(p -> allowedIds.contains(p.getId())).collect(java.util.stream.Collectors.toList());
		 }

		 return players.stream().map(this::toReportResponse).collect(java.util.stream.Collectors.toList());
	 }

	 private com.example.tech_go_api.dto.profileplayer.PlayerReportResponseDTO toReportResponse(ProfilePlayer player) {
		 String responsibleNames = responsibleRepository.findByPlayers_Id(player.getId()).stream()
				 .map(Responsible::getName)
				 .collect(java.util.stream.Collectors.joining(", "));

		 String aulaGrupoName = aulaGrupoRepository.findByPlayers_Id(player.getId()).stream()
				 .map(AulaGrupo::getName)
				 .collect(java.util.stream.Collectors.joining(", "));

		 return new com.example.tech_go_api.dto.profileplayer.PlayerReportResponseDTO(
				 player.getId(),
				 player.getFirstname(),
				 player.getLastname(),
				 player.getRegistrationId(),
				 player.getTurma(),
				 player.getBirthDate(),
				 player.getRg(),
				 player.getCpf(),
				 player.getPhoneNumber(),
				 player.getAddress(),
				 player.getCollege(),
				 player.getPaymentPlan(),
				 responsibleNames.isEmpty() ? null : responsibleNames,
				 aulaGrupoName.isEmpty() ? null : aulaGrupoName,
				 Boolean.TRUE.equals(player.getIsDeleted())
		 );
	 }

	 private String resolveTurma(String turmaFromDto, LocalDate birthDate) {
		 if (turmaFromDto != null && !turmaFromDto.isBlank()) {
			 return turmaFromDto;
		 }
		 if (birthDate == null) {
			 return null;
		 }
		 return String.format("Nascidos %02d", birthDate.getYear() % 100);
	 }

}
