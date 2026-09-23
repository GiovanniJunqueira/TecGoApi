package com.example.tech_go_api.domain.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.tech_go_api.domain.payment.PaymentMethod;
import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
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
@Table(name = "t_sale")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sale implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "school_id")
    @JsonBackReference
    private School school;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;

    @ManyToOne
    @JoinColumn(name = "buyer_player_id", nullable = true)
    @JsonBackReference
    private ProfilePlayer buyerPlayer;

    private String buyerName;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private LocalDate soldAt;
}
