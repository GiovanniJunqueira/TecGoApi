package com.example.tech_go_api.repositories.profileadmin;

import com.example.tech_go_api.domain.profileadmin.ProfileAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileAdminRepository extends JpaRepository<ProfileAdmin, String> {
}
