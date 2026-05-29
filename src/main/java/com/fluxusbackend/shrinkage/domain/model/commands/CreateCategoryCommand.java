package com.fluxusbackend.shrinkage.domain.model.commands;

import java.util.Objects;

public record CreateCategoryCommand(String name) {
    public CreateCategoryCommand {
        Objects.requireNonNull(name, "Category name is required");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }
    }
}

