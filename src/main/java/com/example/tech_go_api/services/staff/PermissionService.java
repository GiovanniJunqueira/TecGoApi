package com.example.tech_go_api.services.staff;

import org.springframework.stereotype.Service;

import com.example.tech_go_api.domain.staff.Permission;
import com.example.tech_go_api.domain.staff.StaffMember;
import com.example.tech_go_api.domain.users.Role;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.repositories.staff.StaffMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final StaffMemberRepository staffMemberRepository;

    public boolean hasPermission(User user, Permission permission) {
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.MASTER) {
            return true;
        }

        if (user.getRole() != Role.STAFF) {
            return false;
        }

        return staffMemberRepository.findById(user.getId())
                .map(StaffMember::getPermissions)
                .map(permissions -> permissions.contains(permission))
                .orElse(false);
    }

    public void requirePermission(User user, Permission permission) {
        if (!hasPermission(user, permission)) {
            throw new IllegalArgumentException("Você não tem permissão para executar essa ação.");
        }
    }
}
