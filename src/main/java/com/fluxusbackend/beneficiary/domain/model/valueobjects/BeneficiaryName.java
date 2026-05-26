package com.fluxusbackend.beneficiary.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record BeneficiaryName(@Column(name = "beneficiary_name", nullable = false, length = 150) String value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public BeneficiaryName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Beneficiary name is required");
        }
    }

    @JsonValue
    public String value() {
        return value;
    }
}


