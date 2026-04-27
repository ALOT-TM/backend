package com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record UserId(@Column(name = "user_id", nullable = false) Long value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public UserId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("User id must be positive");
        }
    }

    @JsonValue
    public Long value() {
        return value;
    }
}

