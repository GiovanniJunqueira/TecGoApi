package com.example.tech_go_api.mappers.school;

import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.dto.school.SchoolCreateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SchoolMapper {
    SchoolCreateDTO toDto(School entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    School toEntity(SchoolCreateDTO dto);
}
