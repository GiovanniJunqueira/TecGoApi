package com.example.tech_go_api.controllers.paymentplan;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.paymentplan.PaymentPlanCreateRequestDTO;
import com.example.tech_go_api.dto.paymentplan.PaymentPlanResponseDTO;
import com.example.tech_go_api.dto.paymentplan.PaymentPlanUpdateRequestDTO;
import com.example.tech_go_api.services.paymentplan.PaymentPlanService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Planos de Pagamento")
@RestController
@RequestMapping("/api/payment-plans")
@RequiredArgsConstructor
public class PaymentPlanController {

    private final PaymentPlanService paymentPlanService;

    private User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping
    public ResponseEntity<PaymentPlanResponseDTO> create(@RequestBody @Valid PaymentPlanCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentPlanService.create(dto, currentUser()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentPlanResponseDTO> update(
            @PathVariable String id, @RequestBody @Valid PaymentPlanUpdateRequestDTO dto) {
        return ResponseEntity.ok(paymentPlanService.update(id, dto, currentUser()));
    }

    @GetMapping
    public ResponseEntity<List<PaymentPlanResponseDTO>> findAll(
            @RequestParam(required = false, defaultValue = "ATIVOS") String status) {
        boolean active = !"INATIVOS".equalsIgnoreCase(status);
        return ResponseEntity.ok(paymentPlanService.findAll(currentUser(), active));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentPlanResponseDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(paymentPlanService.findById(id, currentUser()));
    }

    @PutMapping("/{id}/inativar")
    public ResponseEntity<Void> deactivate(@PathVariable String id) {
        paymentPlanService.deactivate(id, currentUser());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reativar")
    public ResponseEntity<Void> reactivate(@PathVariable String id) {
        paymentPlanService.reactivate(id, currentUser());
        return ResponseEntity.noContent().build();
    }
}
