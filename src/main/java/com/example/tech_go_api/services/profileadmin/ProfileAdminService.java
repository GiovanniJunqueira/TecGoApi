package com.example.tech_go_api.services.profileadmin;

import com.example.tech_go_api.domain.profileadmin.ProfileAdmin;
import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.user.Role;
import com.example.tech_go_api.dto.profileadmin.ProfileAdminCreateRequestDTO;
import com.example.tech_go_api.dto.profileadmin.ProfileAdminResponseDTO;
import com.example.tech_go_api.exceptions.ConflictException;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.mappers.profileadmin.ProfileAdminMapper;
import com.example.tech_go_api.repositories.profileadmin.ProfileAdminRepository;
import com.example.tech_go_api.repositories.school.SchoolRepository;
import com.example.tech_go_api.repositories.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileAdminService {

    private final ProfileAdminRepository profileAdminRepository;
    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final ProfileAdminMapper profileAdminMapper;
    private final PasswordEncoder passwordEncoder;

    public ProfileAdminResponseDTO createProfileAdmin(ProfileAdminCreateRequestDTO dto) {
        userRepository.findByEmail(dto.email()).ifPresent(user -> {
            throw new ConflictException("User with this email already exists");
        });

        School school = null;
        if (dto.schoolId() != null && !dto.schoolId().isEmpty()) {
            school = schoolRepository.findById(dto.schoolId())
                    .orElseThrow(() -> new NotFoundException("School not found"));
        }

        ProfileAdmin newProfileAdmin = new ProfileAdmin();
        newProfileAdmin.setEmail(dto.email());
        newProfileAdmin.setPassword(passwordEncoder.encode(dto.password()));
        newProfileAdmin.setRole(Role.ADMIN);
        newProfileAdmin.setPhone(dto.phone());
        newProfileAdmin.setFirstname(dto.firstname());
        newProfileAdmin.setLastname(dto.lastname());
        newProfileAdmin.setDocument(dto.document());
        newProfileAdmin.setSchool(school);

        ProfileAdmin savedProfileAdmin = profileAdminRepository.save(newProfileAdmin);
        return profileAdminMapper.toDto(savedProfileAdmin);
    }
}
