package com.example.tech_go_api.controllers.school;

import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.dto.school.SchoolCreateDTO;
import com.example.tech_go_api.services.school.SchoolService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Escolas")
@RestController
@RequestMapping("/school")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    @PostMapping()
    public ResponseEntity<School> create(@Valid @RequestBody SchoolCreateDTO dto) {
        School school = schoolService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(school);
    }
}
