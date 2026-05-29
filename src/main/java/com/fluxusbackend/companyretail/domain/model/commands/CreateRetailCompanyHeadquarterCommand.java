package com.fluxusbackend.companyretail.domain.model.commands;

import java.util.Objects;

public record CreateRetailCompanyHeadquarterCommand(
        Long retailCompanyId,
        String description,
        Long addressId
) {
    public CreateRetailCompanyHeadquarterCommand {
        Objects.requireNonNull(retailCompanyId, "Retail company id is required");
        Objects.requireNonNull(description, "Description is required");
        Objects.requireNonNull(addressId, "Address id is required");
        if (retailCompanyId <= 0) {
            throw new IllegalArgumentException("Retail company id must be positive");
        }
        if (description.isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (addressId <= 0) {
            throw new IllegalArgumentException("Address id must be positive");
        }
    }
}

