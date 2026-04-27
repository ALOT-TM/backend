package com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record MermaId(@Column(name = "merma_id", nullable = false) Long value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public MermaId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Merma id must be positive");
        }
    }

    @JsonValue
    public Long value() {
        return value;
    }
}

