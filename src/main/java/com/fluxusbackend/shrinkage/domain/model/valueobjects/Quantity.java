package com.fluxusbackend.shrinkage.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Quantity(@Column(name = "quantity", nullable = false) int amount) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public Quantity {
        if (amount <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }

    @JsonValue
    public int amount() {
        return amount;
    }
}


