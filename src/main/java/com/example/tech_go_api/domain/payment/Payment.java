package com.example.tech_go_api.domain.payment;


import java.io.Serializable;
import java.time.LocalDate;

import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_payment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private boolean status;
    private LocalDate paidAt;
    private String month;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    @JsonBackReference
    private ProfilePlayer profilePlayer;
}