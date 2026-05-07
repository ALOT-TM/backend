package com.fluxusbackend.fluxusbackend.companymanagement.domain.model.commands;

import java.util.Objects;

public record CreateCompanyCommand(String name, String headquarters) {
    public CreateCompanyCommand {
        Objects.requireNonNull(name, "Company name is required");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Company name is required");
        }
    }
}
