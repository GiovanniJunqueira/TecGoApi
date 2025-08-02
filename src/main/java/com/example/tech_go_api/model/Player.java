package com.example.tech_go_api.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor      
@AllArgsConstructor     
@Builder                
@Table(name = "t_player")
public class Player {
	
	@Id
	@GeneratedValue
	private UUID uuid;
	
	private String name;
    private LocalDate birthDate;
    private String rg;
    private String cpf;
    private String phone;
    private String address;
    private String schoolName;
    private String grade;
    private String schoolSchedule;
    private String instagram;
    private String facebook;
    
}
