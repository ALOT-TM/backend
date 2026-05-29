package com.fluxusbackend.shrinkage.domain.model.commands;

import java.util.Objects;

public record UpdateShrinkageReasonCommand(Long shrinkageReasonId, String name) {
    public UpdateShrinkageReasonCommand {
        Objects.requireNonNull(shrinkageReasonId, "Shrinkage reason id is required");
        Objects.requireNonNull(name, "Shrinkage reason name is required");
        if (shrinkageReasonId <= 0) {
            throw new IllegalArgumentException("Shrinkage reason id must be positive");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("Shrinkage reason name is required");
        }
    }
}

