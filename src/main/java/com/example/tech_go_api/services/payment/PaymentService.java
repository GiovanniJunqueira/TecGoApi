package com.example.tech_go_api.services.payment;

import org.springframework.stereotype.Service;

import com.example.tech_go_api.domain.payment.Payment;
import com.example.tech_go_api.domain.payment.PaymentMethod;
import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.example.tech_go_api.domain.responsible.Responsible;
import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.domain.users.profileadmin.ProfileAdmin;
import com.example.tech_go_api.dto.payment.PaymentResponse;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.payment.PaymentRepository;
import com.example.tech_go_api.repositories.profileadmin.ProfileAdminRepository;
import com.example.tech_go_api.repositories.profileplayer.ProfilePlayerRepository;
import com.example.tech_go_api.repositories.responsible.ResponsibleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProfileAdminRepository profileAdminRepository;

    @Autowired
    private ProfilePlayerRepository profilePlayerRepository;

    @Autowired
    private ResponsibleRepository responsibleRepository;

    @Autowired
    private PaymentPricingService paymentPricingService;

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

    // Criar pagamento a partir do ID do aluno, validando que ele pertence à escola do admin logado
    public PaymentResponse createPaymentForPlayer(String playerId, String month, User user) {
        ProfilePlayer player = profilePlayerRepository.findById(playerId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado"));

        if (!player.getSchool().getId().equals(schoolOf(user).getId())) {
            throw new IllegalArgumentException("Este aluno não pertence à sua escola.");
        }

        return toResponse(createPayment(player, month));
    }

    // Marcar pagamento como pago
    public PaymentResponse markAsPaid(String paymentId, PaymentMethod paymentMethod, User user) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Pagamento não encontrado"));

        if (!payment.getProfilePlayer().getSchool().getId().equals(schoolOf(user).getId())) {
            throw new IllegalArgumentException("Este pagamento não pertence à sua escola.");
        }

        LocalDate paidAt = LocalDate.now();
        payment.setStatus(true);
        payment.setPaidAt(paidAt);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(paymentPricingService.calculateAmount(payment.getProfilePlayer().getPaymentPlan(), paidAt));
        return toResponse(paymentRepository.save(payment));
    }

    // Buscar pagamentos da escola, com filtros opcionais de mês, status e busca (aluno/responsável)
    public List<PaymentResponse> search(User user, String month, Boolean pending, String search) {
        Boolean status = Boolean.TRUE.equals(pending) ? Boolean.FALSE : null;
        return paymentRepository.search(schoolOf(user), month, status, search).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Buscar pagamentos de um aluno
    public List<PaymentResponse> getPaymentsByPlayer(String playerId, User user) {
        ProfilePlayer player = profilePlayerRepository.findById(playerId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado"));

        if (!player.getSchool().getId().equals(schoolOf(user).getId())) {
            throw new IllegalArgumentException("Este aluno não pertence à sua escola.");
        }

        return paymentRepository.findByProfilePlayerId(playerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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

    public PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setStatus(payment.isStatus());
        response.setPaidAt(payment.getPaidAt());
        response.setMonth(payment.getMonth());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setAmount(payment.getAmount());

        ProfilePlayer player = payment.getProfilePlayer();
        if (player != null) {
            response.setPlayerId(player.getId());
            response.setPlayerName(fullName(player.getFirstname(), player.getLastname()));

            List<Responsible> responsibles = responsibleRepository.findByPlayers_Id(player.getId());
            String responsibleName = responsibles.stream()
                    .map(Responsible::getName)
                    .collect(Collectors.joining(", "));
            response.setResponsibleName(responsibleName.isEmpty() ? null : responsibleName);
        }

        return response;
    }

    private String fullName(String firstname, String lastname) {
        String first = firstname != null ? firstname.trim() : "";
        String last = lastname != null ? lastname.trim() : "";
        return (first + " " + last).trim();
    }
}
