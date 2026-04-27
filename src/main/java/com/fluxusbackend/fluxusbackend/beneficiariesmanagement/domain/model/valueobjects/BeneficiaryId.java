package com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record BeneficiaryId(@Column(name = "beneficiary_id", nullable = false) Long value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public BeneficiaryId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Beneficiary id must be positive");
        }
    }

    @JsonValue
    public Long value() {
        return value;
    }
}

