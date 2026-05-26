package com.fluxusbackend.beneficiary.domain.model.commands;

import java.util.Objects;

public record RegisterBeneficiaryCommand(
        String name,
        Long institutionTypeId
) {
    public RegisterBeneficiaryCommand {
        Objects.requireNonNull(name, "Name is required");
        Objects.requireNonNull(institutionTypeId, "Institution type id is required");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (institutionTypeId <= 0) {
            throw new IllegalArgumentException("Institution type id must be positive");
        }
    }
}


