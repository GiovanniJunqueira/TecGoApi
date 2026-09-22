package com.example.tech_go_api.services.staff;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.staff.StaffMember;
import com.example.tech_go_api.domain.staff.StaffStatus;
import com.example.tech_go_api.domain.users.Role;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.domain.users.profileadmin.ProfileAdmin;
import com.example.tech_go_api.dto.staff.StaffCreateRequestDTO;
import com.example.tech_go_api.dto.staff.StaffResponseDTO;
import com.example.tech_go_api.dto.staff.StaffUpdateRequestDTO;
import com.example.tech_go_api.exceptions.ConflictException;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.profileadmin.ProfileAdminRepository;
import com.example.tech_go_api.repositories.staff.StaffMemberRepository;
import com.example.tech_go_api.repositories.token.RefreshTokenRepository;
import com.example.tech_go_api.repositories.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffMemberService {

    private final StaffMemberRepository staffMemberRepository;
    private final ProfileAdminRepository profileAdminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    private School schoolOf(User user) {
        ProfileAdmin profileAdmin = profileAdminRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Usuário Admin não encontrado"));
        return profileAdmin.getSchool();
    }

    public StaffResponseDTO create(StaffCreateRequestDTO dto, User user) {
        userRepository.findByEmail(dto.email()).ifPresent(existing -> {
            throw new ConflictException("Já existe um usuário com este e-mail");
        });

        StaffMember staff = new StaffMember();
        staff.setRole(Role.STAFF);
        staff.setEmail(dto.email());
        staff.setPassword(passwordEncoder.encode(dto.password()));
        staff.setFirstname(dto.firstname());
        staff.setLastname(dto.lastname());
        staff.setStaffRole(dto.staffRole());
        staff.setCustomRoleLabel(dto.customRoleLabel());
        staff.setPhone(dto.phone());
        staff.setDocument(dto.document());
        staff.setAdmissionDate(dto.admissionDate());
        staff.setSalary(dto.salary());
        staff.setNotes(dto.notes());
        staff.setStatus(StaffStatus.ACTIVE);
        staff.setSchool(schoolOf(user));
        staff.setPermissions(dto.permissions() != null ? new HashSet<>(dto.permissions()) : new HashSet<>());

        return toResponse(staffMemberRepository.save(staff));
    }

    public List<StaffResponseDTO> findAll(User user) {
        return staffMemberRepository.findBySchool(schoolOf(user)).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public StaffResponseDTO findById(String id, User user) {
        StaffMember staff = findOwnedStaff(id, user);
        return toResponse(staff);
    }

    public StaffResponseDTO update(String id, StaffUpdateRequestDTO dto, User user) {
        StaffMember staff = findOwnedStaff(id, user);

        staff.setFirstname(dto.firstname());
        staff.setLastname(dto.lastname());
        if (dto.password() != null && !dto.password().isBlank()) {
            staff.setPassword(passwordEncoder.encode(dto.password()));
        }
        staff.setStaffRole(dto.staffRole());
        staff.setCustomRoleLabel(dto.customRoleLabel());
        staff.setPhone(dto.phone());
        staff.setDocument(dto.document());
        staff.setAdmissionDate(dto.admissionDate());
        staff.setSalary(dto.salary());
        staff.setNotes(dto.notes());
        if (dto.status() != null) {
            staff.setStatus(dto.status());
        }
        staff.setPermissions(dto.permissions() != null ? new HashSet<>(dto.permissions()) : new HashSet<>());

        return toResponse(staffMemberRepository.save(staff));
    }

    @Transactional
    public void delete(String id, User user) {
        StaffMember staff = findOwnedStaff(id, user);
        refreshTokenRepository.deleteByUser(staff);
        staffMemberRepository.delete(staff);
    }

    private StaffMember findOwnedStaff(String id, User user) {
        StaffMember staff = staffMemberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Profissional não encontrado"));

        if (staff.getSchool() == null || !staff.getSchool().getId().equals(schoolOf(user).getId())) {
            throw new IllegalArgumentException("Este profissional não pertence à sua escola.");
        }

        return staff;
    }

    private StaffResponseDTO toResponse(StaffMember staff) {
        return new StaffResponseDTO(
                staff.getId(),
                staff.getFirstname(),
                staff.getLastname(),
                staff.getEmail(),
                staff.getStaffRole(),
                staff.getCustomRoleLabel(),
                staff.getPhone(),
                staff.getDocument(),
                staff.getAdmissionDate(),
                staff.getSalary(),
                staff.getNotes(),
                staff.getStatus(),
                staff.getPermissions()
        );
    }
}
