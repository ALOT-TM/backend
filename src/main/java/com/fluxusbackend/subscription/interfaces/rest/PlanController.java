package com.fluxusbackend.subscription.interfaces.rest;

import com.fluxusbackend.subscription.domain.model.aggregates.Plan;
import com.fluxusbackend.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import com.fluxusbackend.subscription.interfaces.rest.dto.PlanResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@Tag(name = "Plans", description = "Subscription plans catalog")
public class PlanController {

    private final PlanRepository planRepository;

    public PlanController(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @GetMapping
    @Operation(summary = "List all active subscription plans")
    public List<PlanResponseDto> listPlans() {
        return planRepository.findAll().stream()
                .filter(Plan::isActive)
                .map(PlanResponseDto::from)
                .toList();
    }
}
