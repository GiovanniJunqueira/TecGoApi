package com.example.tech_go_api.repositories.profileplayer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.example.tech_go_api.domain.school.School;

public interface ProfilePlayerRepository extends JpaRepository<ProfilePlayer, String>{
	List<ProfilePlayer> findAllBySchoolAndIsDeletedFalse(School school);
}
