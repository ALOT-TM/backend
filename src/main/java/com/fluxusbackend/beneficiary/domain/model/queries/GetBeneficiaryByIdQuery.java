package com.fluxusbackend.beneficiary.domain.model.queries;

import com.fluxusbackend.beneficiary.domain.model.valueobjects.BeneficiaryId;
import java.util.Objects;

public record GetBeneficiaryByIdQuery(BeneficiaryId beneficiaryId) {
    public GetBeneficiaryByIdQuery {
        Objects.requireNonNull(beneficiaryId, "Beneficiary institution id is required");
    }
}


