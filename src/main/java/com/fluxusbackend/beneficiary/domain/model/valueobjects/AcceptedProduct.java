package com.fluxusbackend.beneficiary.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record AcceptedProduct(@Column(name = "accepted_product", nullable = false, length = 120) String value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public AcceptedProduct {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Accepted product is required");
        }
    }

    @JsonValue
    public String value() {
        return value;
    }
}


