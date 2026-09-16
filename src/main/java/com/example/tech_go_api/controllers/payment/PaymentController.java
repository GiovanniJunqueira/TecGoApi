package com.example.tech_go_api.controllers.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.tech_go_api.domain.payment.PaymentMethod;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.payment.MarkAsPaidRequestDTO;
import com.example.tech_go_api.dto.payment.PaymentResponse;
import com.example.tech_go_api.services.payment.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    private User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // Criar pagamento para um aluno em um mês
    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestParam String playerId,
            @RequestParam String month) {
        return ResponseEntity.ok(paymentService.createPaymentForPlayer(playerId, month, currentUser()));
    }

    // Marcar pagamento como pago
    @PutMapping("/{id}/pay")
    public ResponseEntity<PaymentResponse> markAsPaid(@PathVariable String id, @RequestBody(required = false) MarkAsPaidRequestDTO body) {
        PaymentMethod method = body != null ? body.paymentMethod() : null;
        return ResponseEntity.ok(paymentService.markAsPaid(id, method, currentUser()));
    }

    // Buscar pagamentos da escola, com filtros opcionais de mês, status (pendentes) e busca por aluno/responsável
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> search(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Boolean pending,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(paymentService.search(currentUser(), month, pending, search));
    }

    // Buscar pagamentos de um aluno
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByPlayer(@PathVariable String playerId) {
        return ResponseEntity.ok(paymentService.getPaymentsByPlayer(playerId, currentUser()));
    }

    // Deletar pagamento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable String id) {
        paymentService.deletePayment(id, currentUser());
        return ResponseEntity.noContent().build();
    }
}
