package com.example.tech_go_api.controllers.dashboard;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.dashboard.DashboardSummaryResponse;
import com.example.tech_go_api.services.dashboard.DashboardService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Dashboard")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    private User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(@RequestParam(required = false) String month) {
        return ResponseEntity.ok(dashboardService.getSummary(currentUser(), month));
    }
}
