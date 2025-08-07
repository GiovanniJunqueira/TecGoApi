package com.example.tech_go_api.mappers.profileadmin;

import com.example.tech_go_api.domain.profileadmin.ProfileAdmin;
import com.example.tech_go_api.dto.profileadmin.ProfileAdminResponseDTO;

import org.springframework.stereotype.Component;

@Component
public class ProfileAdminMapper {

    public ProfileAdminResponseDTO toDto(ProfileAdmin entity) {
        if (entity == null) {
            return null;
        }
        String schoolId = (entity.getSchool() != null) ? entity.getSchool().getId() : null;
        return new ProfileAdminResponseDTO(
                entity.getId(),
                entity.getEmail(),
                entity.getRole(),
                entity.getPhone(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getDocument(),
                schoolId
        );
    }


}
