package com.fluxusbackend.subscription.domain.model.commands;

import java.util.Objects;

public record CreateSubscriptionCommand(
        Long retailCompanyId,
        Short planId
) {
    public CreateSubscriptionCommand {
        Objects.requireNonNull(retailCompanyId, "Retail company id is required");
        Objects.requireNonNull(planId, "Plan id is required");
        if (retailCompanyId <= 0) {
            throw new IllegalArgumentException("Retail company id must be positive");
        }
        if (planId <= 0) {
            throw new IllegalArgumentException("Plan id must be positive");
        }
    }
}
