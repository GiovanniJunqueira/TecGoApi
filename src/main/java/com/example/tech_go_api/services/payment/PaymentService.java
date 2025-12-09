package com.example.tech_go_api.services.payment;

import org.springframework.stereotype.Service;

import com.example.tech_go_api.domain.payment.Payment;
import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.example.tech_go_api.repositories.payment.PaymentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Criar pagamento para um aluno em um mês
    public Payment createPayment(ProfilePlayer player, String month) {
        Payment payment = new Payment();
        payment.setProfilePlayer(player);
        payment.setMonth(month);
        payment.setStatus(false); // inicialmente não pago
        payment.setPaidAt(null);
        return paymentRepository.save(payment);
    }

    // Marcar pagamento como pago
    public Payment markAsPaid(String paymentId) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            payment.setStatus(true);
            payment.setPaidAt(LocalDate.now());
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Pagamento não encontrado");
    }

    // Buscar pagamentos por mês
    public List<Payment> getPaymentsByMonth(String month) {
        return paymentRepository.findByMonth(month);
    }

    // Buscar pagamentos de um aluno
    public List<Payment> getPaymentsByPlayer(String playerId) {
        return paymentRepository.findByProfilePlayerId(playerId);
    }

    // Buscar pagamentos pendentes de um mês
    public List<Payment> getPendingPayments(String month) {
        return paymentRepository.findByMonthAndStatus(month, false);
    }

    // Deletar pagamento
    public void deletePayment(String paymentId) {
        paymentRepository.deleteById(paymentId);
    }
}
