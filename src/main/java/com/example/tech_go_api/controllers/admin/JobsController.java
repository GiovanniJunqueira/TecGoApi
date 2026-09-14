package com.example.tech_go_api.controllers.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tech_go_api.services.scheduled.PaymentScheduledJobs;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Master - Jobs")
@RestController
@RequestMapping("/admin/jobs")
@RequiredArgsConstructor
public class JobsController {

    private final PaymentScheduledJobs paymentScheduledJobs;

    @PostMapping("/gerar-mensalidades")
    public ResponseEntity<Void> gerarMensalidades() {
        paymentScheduledJobs.generateMonthlyPayments();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/limpar-pagamentos-antigos")
    public ResponseEntity<Void> limparPagamentosAntigos() {
        paymentScheduledJobs.purgeOldPaidPayments();
        return ResponseEntity.noContent().build();
    }
}
