package com.example.tech_go_api.dto.school;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private String cnpj;
    private String address;
    private String city;
    private String logoUrl; 
}
