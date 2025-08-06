package com.example.tech_go_api.controllers.profileadmin;

import com.example.tech_go_api.dto.profileadmin.ProfileAdminCreateRequestDTO;
import com.example.tech_go_api.dto.profileadmin.ProfileAdminResponseDTO;
import com.example.tech_go_api.services.profileadmin.ProfileAdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin Profile")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ProfileAdminController {

    private final ProfileAdminService profileAdminService;

    @PostMapping
    public ResponseEntity<ProfileAdminResponseDTO> createProfileAdmin(@RequestBody @Valid ProfileAdminCreateRequestDTO dto) {
        ProfileAdminResponseDTO newProfileAdmin = profileAdminService.createProfileAdmin(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProfileAdmin);
    }
}
