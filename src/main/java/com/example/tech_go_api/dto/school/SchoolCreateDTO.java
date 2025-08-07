package com.example.tech_go_api.dto.school;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SchoolCreateDTO {
    @NotBlank(message = "Nome da escola é obrigatório")
    private String name;

    @NotBlank(message = "CNPJ é obrigatório")
    private String cnpj;

    private String address;

    private String city;
}
