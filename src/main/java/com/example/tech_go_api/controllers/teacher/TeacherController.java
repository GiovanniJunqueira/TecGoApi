package com.example.tech_go_api.controllers.teacher;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tech_go_api.domain.teacher.TeacherStatus;
import com.example.tech_go_api.dto.teacher.TeacherRequest;
import com.example.tech_go_api.dto.teacher.TeacherResponse;
import com.example.tech_go_api.services.teacher.TeacherService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Professores")
@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    public ResponseEntity<List<TeacherResponse>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) TeacherStatus status
    ) {
        return ResponseEntity.ok(teacherService.findAll(name, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(teacherService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TeacherResponse> create(@RequestBody @Valid TeacherRequest request) {
        return ResponseEntity.ok(teacherService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponse> update(@PathVariable String id,
                                                  @RequestBody @Valid TeacherRequest request) {
        return ResponseEntity.ok(teacherService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
