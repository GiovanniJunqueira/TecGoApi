package com.example.tech_go_api.services.school;

import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.dto.school.SchoolCreateDTO;
import com.example.tech_go_api.mappers.school.SchoolMapper;
import com.example.tech_go_api.exceptions.ConflictException;
import com.example.tech_go_api.repositories.school.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final SchoolMapper schoolMapper;

    public School create(SchoolCreateDTO dto) {
        schoolRepository.findByCnpj(dto.getCnpj()).ifPresent(school -> {
            throw new ConflictException("Escola com este CNPJ já existe");
        });

        School school = schoolMapper.toEntity(dto);
        return schoolRepository.save(school);
    }
}
