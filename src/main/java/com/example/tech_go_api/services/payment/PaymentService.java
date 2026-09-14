package com.example.tech_go_api.services.payment;

import org.springframework.stereotype.Service;

import com.example.tech_go_api.domain.payment.Payment;
import com.example.tech_go_api.domain.payment.PaymentMethod;
import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.domain.users.profileadmin.ProfileAdmin;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.payment.PaymentRepository;
import com.example.tech_go_api.repositories.profileadmin.ProfileAdminRepository;
import com.example.tech_go_api.repositories.profileplayer.ProfilePlayerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProfileAdminRepository profileAdminRepository;

    @Autowired
    private ProfilePlayerRepository profilePlayerRepository;

    private School schoolOf(User user) {
        ProfileAdmin profileAdmin = profileAdminRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Usuário Admin não encontrado"));
        return profileAdmin.getSchool();
    }

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
    public Payment markAsPaid(String paymentId, PaymentMethod paymentMethod, User user) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Pagamento não encontrado"));

        if (!payment.getProfilePlayer().getSchool().getId().equals(schoolOf(user).getId())) {
            throw new IllegalArgumentException("Este pagamento não pertence à sua escola.");
        }

        payment.setStatus(true);
        payment.setPaidAt(LocalDate.now());
        payment.setPaymentMethod(paymentMethod);
        return paymentRepository.save(payment);
    }

    // Buscar todos os pagamentos da escola
    public List<Payment> getAllPayments(User user) {
        return paymentRepository.findByProfilePlayerSchool(schoolOf(user));
    }

    // Buscar pagamentos por mês
    public List<Payment> getPaymentsByMonth(String month, User user) {
        return paymentRepository.findByProfilePlayerSchoolAndMonth(schoolOf(user), month);
    }

    // Buscar pagamentos de um aluno
    public List<Payment> getPaymentsByPlayer(String playerId, User user) {
        ProfilePlayer player = profilePlayerRepository.findById(playerId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado"));

        if (!player.getSchool().getId().equals(schoolOf(user).getId())) {
            throw new IllegalArgumentException("Este aluno não pertence à sua escola.");
        }

        return paymentRepository.findByProfilePlayerId(playerId);
    }

    // Buscar pagamentos pendentes de um mês
    public List<Payment> getPendingPayments(String month, User user) {
        return paymentRepository.findByProfilePlayerSchoolAndMonthAndStatus(schoolOf(user), month, false);
    }

    // Deletar pagamento
    public void deletePayment(String paymentId, User user) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Pagamento não encontrado"));

        if (!payment.getProfilePlayer().getSchool().getId().equals(schoolOf(user).getId())) {
            throw new IllegalArgumentException("Este pagamento não pertence à sua escola.");
        }

        paymentRepository.deleteById(paymentId);
    }
}
