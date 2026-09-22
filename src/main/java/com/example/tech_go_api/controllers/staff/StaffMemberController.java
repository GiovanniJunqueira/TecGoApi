package com.example.tech_go_api.controllers.staff;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.staff.StaffCreateRequestDTO;
import com.example.tech_go_api.dto.staff.StaffResponseDTO;
import com.example.tech_go_api.dto.staff.StaffUpdateRequestDTO;
import com.example.tech_go_api.services.staff.StaffMemberService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Profissionais")
@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffMemberController {

    private final StaffMemberService staffMemberService;

    private User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping
    public ResponseEntity<StaffResponseDTO> create(@RequestBody @Valid StaffCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(staffMemberService.create(dto, currentUser()));
    }

    @GetMapping
    public ResponseEntity<List<StaffResponseDTO>> findAll() {
        return ResponseEntity.ok(staffMemberService.findAll(currentUser()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(staffMemberService.findById(id, currentUser()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> update(@PathVariable String id, @RequestBody @Valid StaffUpdateRequestDTO dto) {
        return ResponseEntity.ok(staffMemberService.update(id, dto, currentUser()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        staffMemberService.delete(id, currentUser());
        return ResponseEntity.noContent().build();
    }
}
