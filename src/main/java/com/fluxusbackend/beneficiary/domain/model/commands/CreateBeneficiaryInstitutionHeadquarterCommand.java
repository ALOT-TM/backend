package com.fluxusbackend.beneficiary.domain.model.commands;

import java.util.Objects;

public record CreateBeneficiaryInstitutionHeadquarterCommand(
        Long beneficiaryInstitutionId,
        String description,
        Long addressId
) {
    public CreateBeneficiaryInstitutionHeadquarterCommand {
        Objects.requireNonNull(beneficiaryInstitutionId, "Beneficiary institution id is required");
        Objects.requireNonNull(description, "Description is required");
        Objects.requireNonNull(addressId, "Address id is required");
        if (beneficiaryInstitutionId <= 0) {
            throw new IllegalArgumentException("Beneficiary institution id must be positive");
        }
        if (description.isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (addressId <= 0) {
            throw new IllegalArgumentException("Address id must be positive");
        }
    }
}

