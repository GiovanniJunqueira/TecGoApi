package com.example.tech_go_api.dto.product;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductUpdateRequestDTO {

    @NotBlank(message = "Nome do produto é obrigatório")
    private String name;

    private String description;

    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser maior que zero")
    private BigDecimal price;

    private MultipartFile photoFile;
}
