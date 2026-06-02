package com.fluxusbackend.subscription.interfaces.rest.dto;

import java.math.BigDecimal;

public record PlanResponseDto(
        Short planId,
        String name,
        BigDecimal price,
        Integer maxUsers,
        Integer maxStorage
) {
    public static PlanResponseDto from(com.fluxusbackend.subscription.domain.model.aggregates.Plan plan) {
        return new PlanResponseDto(
                plan.getPlanId(),
                plan.getName(),
                plan.getPrice(),
                plan.getMaxUsers(),
                plan.getMaxStorage()
        );
    }
}
