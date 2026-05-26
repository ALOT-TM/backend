package com.fluxusbackend.beneficiary.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Address(@Column(name = "address", nullable = false, length = 200) String value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public Address {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Address is required");
        }
    }

    @JsonValue
    public String value() {
        return value;
    }
}


