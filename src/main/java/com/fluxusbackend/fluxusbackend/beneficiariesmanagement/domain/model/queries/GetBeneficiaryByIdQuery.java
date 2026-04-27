package com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.queries;

import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.valueobjects.BeneficiaryId;
import java.util.Objects;

public record GetBeneficiaryByIdQuery(BeneficiaryId beneficiaryId) {
    public GetBeneficiaryByIdQuery {
        Objects.requireNonNull(beneficiaryId, "Beneficiary id is required");
    }
}

