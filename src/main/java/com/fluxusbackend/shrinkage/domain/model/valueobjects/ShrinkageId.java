package com.fluxusbackend.shrinkage.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ShrinkageId(@Column(name = "shrinkage_id", nullable = false) Long value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public ShrinkageId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Shrinkage id must be positive");
        }
    }

    @JsonValue
    public Long value() {
        return value;
    }
}


