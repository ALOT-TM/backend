package com.fluxusbackend.shrinkage.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ProductName(@Column(name = "product_name", nullable = false, length = 120) String value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public ProductName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
    }

    @JsonValue
    public String value() {
        return value;
    }
}


