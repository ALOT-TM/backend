package com.fluxusbackend.donationlogistics.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ShrinkageReferenceId(@Column(name = "shrinkage_id", nullable = false) Long value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public ShrinkageReferenceId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Shrinkage reference id must be positive");
        }
    }

    @JsonValue
    public Long value() {
        return value;
    }
}
