package com.fluxusbackend.beneficiary.domain.model.commands;

import com.fluxusbackend.beneficiary.domain.model.valueobjects.BeneficiaryId;
import java.util.Objects;

public record UpdateBeneficiaryInfoCommand(
        BeneficiaryId beneficiaryId,
        String name,
        Long institutionTypeId
) {
    public UpdateBeneficiaryInfoCommand {
        Objects.requireNonNull(beneficiaryId, "Beneficiary institution id is required");
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


