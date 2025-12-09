package com.example.tech_go_api.controllers.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.tech_go_api.domain.payment.Payment;
import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.example.tech_go_api.dto.payment.PaymentResponse;
import com.example.tech_go_api.services.payment.PaymentService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Criar pagamento para um aluno em um mês
    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestParam String playerId,
            @RequestParam String month) {
        ProfilePlayer player = new ProfilePlayer();
        player.setId(playerId);
        Payment payment = paymentService.createPayment(player, month);
        return ResponseEntity.ok(new PaymentResponse(payment));
    }

    // Marcar pagamento como pago
    @PutMapping("/{id}/pay")
    public ResponseEntity<PaymentResponse> markAsPaid(@PathVariable String id) {
        Payment payment = paymentService.markAsPaid(id);
        return ResponseEntity.ok(new PaymentResponse(payment));
    }

    // Buscar pagamentos por mês
    @GetMapping("/month/{month}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByMonth(@PathVariable String month) {
        List<PaymentResponse> payments = paymentService.getPaymentsByMonth(month)
                .stream().map(PaymentResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(payments);
    }

    // Buscar pagamentos de um aluno
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByPlayer(@PathVariable String playerId) {
        List<PaymentResponse> payments = paymentService.getPaymentsByPlayer(playerId)
                .stream().map(PaymentResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(payments);
    }

    // Buscar pagamentos pendentes de um mês
    @GetMapping("/month/{month}/pending")
    public ResponseEntity<List<PaymentResponse>> getPendingPayments(@PathVariable String month) {
        List<PaymentResponse> payments = paymentService.getPendingPayments(month)
                .stream().map(PaymentResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(payments);
    }

    // Deletar pagamento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable String id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}