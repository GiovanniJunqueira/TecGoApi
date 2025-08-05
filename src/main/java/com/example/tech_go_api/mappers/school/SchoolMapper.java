package com.example.tech_go_api.mappers.school;

import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.dto.school.SchoolCreateDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SchoolMapper {
    SchoolCreateDTO toDto(School entity);
    School toEntity(SchoolCreateDTO dto);
}
