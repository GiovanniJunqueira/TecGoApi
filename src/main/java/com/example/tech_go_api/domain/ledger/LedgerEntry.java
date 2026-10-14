package com.example.tech_go_api.domain.ledger;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.tech_go_api.domain.payment.PaymentMethod;
import com.example.tech_go_api.domain.school.School;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_ledger_entry")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LedgerEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "school_id")
    @JsonBackReference
    private School school;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private LedgerCategory category;

    private BigDecimal amount;

    private LocalDate date;

    private String description;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
}
