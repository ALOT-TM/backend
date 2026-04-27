package com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.commands;

import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.valueobjects.BeneficiaryId;
import java.util.Objects;

public record ActivateBeneficiaryCommand(BeneficiaryId beneficiaryId) {
    public ActivateBeneficiaryCommand {
        Objects.requireNonNull(beneficiaryId, "Beneficiary id is required");
    }
}

