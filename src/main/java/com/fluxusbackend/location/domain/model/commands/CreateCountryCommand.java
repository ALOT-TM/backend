package com.fluxusbackend.location.domain.model.commands;

import java.util.Objects;

public record CreateCountryCommand(String name) {
    public CreateCountryCommand {
        Objects.requireNonNull(name, "Country name is required");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Country name is required");
        }
    }
}

