package com.example.tech_go_api.dto.school;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SchoolCreateDTO {
    @NotBlank(message = "Nome da escola é obrigatório")
    private String name;

    @NotBlank(message = "CNPJ é obrigatório")
    private String cnpj;

    @NotBlank(message = "Endereço é obrigatório")
    private String address;

    @NotBlank(message = "Cidade é obrigatória")
    private String city;
    
    private MultipartFile logoFile;
}
