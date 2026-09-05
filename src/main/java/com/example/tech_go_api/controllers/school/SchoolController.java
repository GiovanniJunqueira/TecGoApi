package com.example.tech_go_api.controllers.school;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.school.SchoolCreateDTO;
import com.example.tech_go_api.dto.school.SchoolResponseDTO;
import com.example.tech_go_api.dto.school.SchoolUpdateDTO;
import com.example.tech_go_api.exceptions.BusinessException;
import com.example.tech_go_api.services.school.SchoolService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Escolas")
@RestController
@RequestMapping("/school")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

  
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<School> create(
            @Valid @ModelAttribute SchoolCreateDTO dto,
            @RequestParam(value = "logo", required = false) MultipartFile logoFile) {
        
        if (logoFile == null || logoFile.isEmpty()) {
            throw new BusinessException("Logo da escola obrigatório");
        }
        dto.setLogoFile(logoFile);
        
        School school = schoolService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(school);
    }
    
    
    
    @GetMapping
    public ResponseEntity<SchoolResponseDTO> find() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SchoolResponseDTO response = schoolService.findByUser(user);
        return ResponseEntity.ok(response);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SchoolResponseDTO> update(
            @Valid @ModelAttribute SchoolUpdateDTO dto,
            @RequestParam(value = "logo", required = false) MultipartFile logoFile) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dto.setLogoFile(logoFile);
        SchoolResponseDTO response = schoolService.update(user, dto);
        return ResponseEntity.ok(response);
    }

}
