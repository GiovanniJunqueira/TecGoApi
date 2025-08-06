package com.example.tech_go_api.dto.profileadmin;

import com.example.tech_go_api.domain.user.Role;

public record ProfileAdminResponseDTO(
        String id,
        String email,
        Role role,
        String phone,
        String firstname,
        String lastname,
        String document,
        String schoolId
) {
}
