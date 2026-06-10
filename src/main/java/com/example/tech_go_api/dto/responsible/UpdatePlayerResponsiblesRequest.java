package com.example.tech_go_api.dto.responsible;

import java.util.List;

public record UpdatePlayerResponsiblesRequest(
    List<String> responsibleIds
) {}
