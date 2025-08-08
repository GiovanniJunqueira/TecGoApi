package com.example.tech_go_api.services.school;

import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.dto.school.SchoolCreateDTO;
import com.example.tech_go_api.exceptions.ConflictException;
import com.example.tech_go_api.repositories.school.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;

    public School create(SchoolCreateDTO dto) {
        schoolRepository.findByCnpj(dto.getCnpj()).ifPresent(school -> {
            throw new ConflictException("Escola com este CNPJ já existe");
        });

        School school = new School();
        school.setName(dto.getName());
        school.setCnpj(dto.getCnpj());
        school.setAddress(dto.getAddress());
        school.setCity(dto.getCity());
        return schoolRepository.save(school);
    }
}
