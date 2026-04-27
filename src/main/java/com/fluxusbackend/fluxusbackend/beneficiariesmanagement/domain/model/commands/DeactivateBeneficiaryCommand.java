package com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.commands;

import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.valueobjects.BeneficiaryId;
import java.util.Objects;

public record DeactivateBeneficiaryCommand(BeneficiaryId beneficiaryId) {
    public DeactivateBeneficiaryCommand {
        Objects.requireNonNull(beneficiaryId, "Beneficiary id is required");
    }
}

