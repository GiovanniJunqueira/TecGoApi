package com.example.tech_go_api.services.teacher;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tech_go_api.domain.teacher.Teacher;
import com.example.tech_go_api.domain.teacher.TeacherStatus;
import com.example.tech_go_api.dto.teacher.TeacherRequest;
import com.example.tech_go_api.dto.teacher.TeacherResponse;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.teacher.TeacherRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public List<TeacherResponse> findAll(String name, TeacherStatus status) {
        List<Teacher> teachers;

        if (name != null && !name.isBlank()) {
            teachers = teacherRepository.findByNameContainingIgnoreCase(name);
        } else if (status != null) {
            teachers = teacherRepository.findByStatus(status);
        } else {
            teachers = teacherRepository.findAll();
        }

        return teachers.stream().map(TeacherResponse::new).toList();
    }

    public TeacherResponse findById(String id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Professor não encontrado"));
        return new TeacherResponse(teacher);
    }

    public TeacherResponse create(TeacherRequest request) {
        Teacher teacher = new Teacher();
        updateEntityFromRequest(teacher, request);
        Teacher saved = teacherRepository.save(teacher);
        return new TeacherResponse(saved);
    }

    public TeacherResponse update(String id, TeacherRequest request) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Professor não encontrado"));
        updateEntityFromRequest(teacher, request);
        Teacher saved = teacherRepository.save(teacher);
        return new TeacherResponse(saved);
    }

    public void delete(String id) {
        teacherRepository.deleteById(id);
    }

    private void updateEntityFromRequest(Teacher teacher, TeacherRequest request) {
        teacher.setName(request.name());
        teacher.setEmail(request.email());
        teacher.setPhone(request.phone());
        teacher.setRole(request.role());
        teacher.setAdmissionDate(request.admissionDate());
        teacher.setStatus(request.status());
        teacher.setSalary(request.salary());
        teacher.setNotes(request.notes());
    }
}
