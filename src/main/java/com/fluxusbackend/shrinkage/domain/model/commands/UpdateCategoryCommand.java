package com.fluxusbackend.shrinkage.domain.model.commands;

import java.util.Objects;

public record UpdateCategoryCommand(Long categoryId, String name) {
    public UpdateCategoryCommand {
        Objects.requireNonNull(categoryId, "Category id is required");
        Objects.requireNonNull(name, "Category name is required");
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Category id must be positive");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }
    }
}

