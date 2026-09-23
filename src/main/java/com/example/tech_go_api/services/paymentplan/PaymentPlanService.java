package com.example.tech_go_api.services.paymentplan;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.tech_go_api.domain.paymentplan.PaymentPlan;
import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.paymentplan.PaymentPlanCreateRequestDTO;
import com.example.tech_go_api.dto.paymentplan.PaymentPlanResponseDTO;
import com.example.tech_go_api.dto.paymentplan.PaymentPlanUpdateRequestDTO;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.paymentplan.PaymentPlanRepository;
import com.example.tech_go_api.services.school.SchoolResolverService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentPlanService {

    private final PaymentPlanRepository paymentPlanRepository;
    private final SchoolResolverService schoolResolverService;

    public PaymentPlanResponseDTO create(PaymentPlanCreateRequestDTO dto, User user) {
        School school = schoolResolverService.schoolOf(user);

        PaymentPlan plan = new PaymentPlan();
        plan.setSchool(school);
        plan.setName(dto.name());
        plan.setPriceOnTime(dto.priceOnTime());
        plan.setPriceLate(dto.priceLate());
        plan.setActive(true);

        return toResponse(paymentPlanRepository.save(plan));
    }

    public PaymentPlanResponseDTO update(String id, PaymentPlanUpdateRequestDTO dto, User user) {
        PaymentPlan plan = findOwnedPlan(id, user);
        plan.setName(dto.name());
        plan.setPriceOnTime(dto.priceOnTime());
        plan.setPriceLate(dto.priceLate());
        return toResponse(paymentPlanRepository.save(plan));
    }

    public List<PaymentPlanResponseDTO> findAll(User user, boolean active) {
        School school = schoolResolverService.schoolOf(user);
        return paymentPlanRepository.findBySchoolAndActive(school, active).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PaymentPlanResponseDTO findById(String id, User user) {
        return toResponse(findOwnedPlan(id, user));
    }

    public void deactivate(String id, User user) {
        PaymentPlan plan = findOwnedPlan(id, user);
        plan.setActive(false);
        paymentPlanRepository.save(plan);
    }

    public void reactivate(String id, User user) {
        PaymentPlan plan = findOwnedPlan(id, user);
        plan.setActive(true);
        paymentPlanRepository.save(plan);
    }

    public PaymentPlan findOwnedPlan(String id, User user) {
        School school = schoolResolverService.schoolOf(user);
        PaymentPlan plan = paymentPlanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Plano de pagamento não encontrado"));

        if (!plan.getSchool().getId().equals(school.getId())) {
            throw new IllegalArgumentException("Este plano não pertence à sua escola.");
        }

        return plan;
    }

    private PaymentPlanResponseDTO toResponse(PaymentPlan plan) {
        return new PaymentPlanResponseDTO(
                plan.getId(),
                plan.getName(),
                plan.getPriceOnTime(),
                plan.getPriceLate(),
                plan.isActive()
        );
    }
}
