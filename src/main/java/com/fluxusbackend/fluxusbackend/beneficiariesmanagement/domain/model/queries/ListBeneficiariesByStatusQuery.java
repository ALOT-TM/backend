package com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.queries;

import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.enums.BeneficiaryStatus;
import java.util.Objects;

public record ListBeneficiariesByStatusQuery(BeneficiaryStatus status) {
    public ListBeneficiariesByStatusQuery {
        Objects.requireNonNull(status, "Status is required");
    }
}

