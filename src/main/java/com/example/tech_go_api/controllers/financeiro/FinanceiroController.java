package com.example.tech_go_api.controllers.financeiro;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.financeiro.FinanceiroSummaryResponse;
import com.example.tech_go_api.services.financeiro.FinanceiroService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Financeiro")
@RestController
@RequestMapping("/api/financeiro")
@RequiredArgsConstructor
public class FinanceiroController {

    private final FinanceiroService financeiroService;

    @GetMapping("/resumo")
    public ResponseEntity<FinanceiroSummaryResponse> getSummary(@RequestParam(required = false) String month) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(financeiroService.getSummary(user, month));
    }
}
