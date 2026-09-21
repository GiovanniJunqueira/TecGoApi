package com.example.tech_go_api.repositories.profileplayer;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.example.tech_go_api.domain.school.School;

public interface ProfilePlayerRepository extends JpaRepository<ProfilePlayer, String>{

	List<ProfilePlayer> findBySchoolAndIsDeletedFalse(School school);
	List<ProfilePlayer> findByIsDeletedTrueAndInactiveSinceBefore(LocalDate cutoff);
	long countBySchoolAndIsDeletedFalse(School school);

	@Query("SELECT p FROM ProfilePlayer p WHERE p.school = :school AND p.isDeleted = :isDeleted AND ("
			+ ":search IS NULL OR :search = '' "
			+ "OR LOWER(p.firstname) LIKE LOWER(CONCAT('%', :search, '%')) "
			+ "OR LOWER(p.lastname) LIKE LOWER(CONCAT('%', :search, '%')) "
			+ "OR LOWER(p.registrationId) LIKE LOWER(CONCAT('%', :search, '%'))) "
			+ "AND (:turma IS NULL OR :turma = '' OR LOWER(p.turma) LIKE LOWER(CONCAT('%', :turma, '%')))")
	Page<ProfilePlayer> searchBySchoolAndIsDeleted(
			@Param("school") School school,
			@Param("isDeleted") boolean isDeleted,
			@Param("search") String search,
			@Param("turma") String turma,
			Pageable pageable);
}
