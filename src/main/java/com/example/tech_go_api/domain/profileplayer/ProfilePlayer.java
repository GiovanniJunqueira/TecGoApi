package com.example.tech_go_api.domain.profileplayer;

import java.time.LocalDate;

import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.domain.users.profileadmin.ProfileAdmin;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_profile_player")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "user_id")
public class ProfilePlayer extends User {
	
	private String firstname;
	private String lastname;
	private LocalDate birthDate; 
	private String rg;
	private String cpf;
	private String phoneNumber;
	private String address;
	private String addressNumber;
	private String addressNeighborhood;
	private String addressComplement;
	private String postcode;
	private String college;
	private String collegeAddress;
	private String collegeNeighborhood;
	private String collegeComplement;
	private String collegePostcode;
	private String collegePhone;
	private String collegeSeries;
	private String collegeTime;
	private String origin;
	private String registrationId;

	@ManyToOne
    @JoinColumn(name = "school_id", nullable = true)
    @JsonBackReference
    private School school;
}
