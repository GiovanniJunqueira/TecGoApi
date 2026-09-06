package com.example.tech_go_api.repositories.profileplayer;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.example.tech_go_api.domain.school.School;

public interface ProfilePlayerRepository extends JpaRepository<ProfilePlayer, String>{
	Page<ProfilePlayer> findAllBySchoolAndIsDeletedFalse(School school, Pageable pageable);
	Page<ProfilePlayer> findAllBySchoolAndIsDeletedTrue(School school, Pageable pageable);
}
