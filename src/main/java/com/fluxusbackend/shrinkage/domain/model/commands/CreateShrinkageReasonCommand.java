package com.fluxusbackend.shrinkage.domain.model.commands;

import java.util.Objects;

public record CreateShrinkageReasonCommand(String name) {
    public CreateShrinkageReasonCommand {
        Objects.requireNonNull(name, "Shrinkage reason name is required");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Shrinkage reason name is required");
        }
    }
}

