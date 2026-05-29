package com.fluxusbackend.beneficiary.domain.model.commands;

import java.util.Objects;

public record RegisterInstitutionTypeCommand(String name) {
    public RegisterInstitutionTypeCommand {
        Objects.requireNonNull(name, "Institution type name is required");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Institution type name is required");
        }
    }
}

