package com.example.tech_go_api.services.school;

import org.springframework.stereotype.Service;

import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.staff.StaffMember;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.domain.users.profileadmin.ProfileAdmin;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.profileadmin.ProfileAdminRepository;
import com.example.tech_go_api.repositories.staff.StaffMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchoolResolverService {

    private final ProfileAdminRepository profileAdminRepository;
    private final StaffMemberRepository staffMemberRepository;

    public School schoolOf(User user) {
        return profileAdminRepository.findById(user.getId())
                .map(ProfileAdmin::getSchool)
                .or(() -> staffMemberRepository.findById(user.getId()).map(StaffMember::getSchool))
                .orElseThrow(() -> new NotFoundException("Usuário não vinculado a uma escola"));
    }
}
