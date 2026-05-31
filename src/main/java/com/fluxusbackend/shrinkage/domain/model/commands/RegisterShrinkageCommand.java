package com.fluxusbackend.shrinkage.domain.model.commands;

import java.time.LocalDate;
import java.util.Objects;

public record RegisterShrinkageCommand(
        Long retailCompanyHeadquarterId,
        Long categoryId,
        Long shrinkageReasonId,
        String name,
        Integer quantity,
        LocalDate expirationDate,
        String specificReason,
        LocalDate pickupDate,
        Double shrinkageValue
) {
    public RegisterShrinkageCommand {
        Objects.requireNonNull(retailCompanyHeadquarterId, "Retail company headquarter id is required");
        Objects.requireNonNull(categoryId, "Category id is required");
        Objects.requireNonNull(shrinkageReasonId, "Shrinkage reason id is required");
        Objects.requireNonNull(name, "Name is required");
        Objects.requireNonNull(quantity, "Quantity is required");
        Objects.requireNonNull(shrinkageValue, "Shrinkage value is required");
        if (retailCompanyHeadquarterId <= 0) {
            throw new IllegalArgumentException("Retail company headquarter id must be positive");
        }
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Category id must be positive");
        }
        if (shrinkageReasonId <= 0) {
            throw new IllegalArgumentException("Shrinkage reason id must be positive");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (shrinkageValue < 0.0) {
            throw new IllegalArgumentException("Shrinkage value must be non-negative");
        }
    }
}


