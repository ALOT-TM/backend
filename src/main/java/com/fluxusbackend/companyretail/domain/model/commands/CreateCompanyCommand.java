package com.fluxusbackend.companyretail.domain.model.commands;

import java.util.Objects;

public record CreateCompanyCommand(String name) {
    public CreateCompanyCommand {
        Objects.requireNonNull(name, "Company name is required");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Company name is required");
        }
    }
}

