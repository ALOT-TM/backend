package com.fluxusbackend.authaccess.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record PasswordHash(@Column(name = "password_hash", nullable = false, length = 100) String value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public PasswordHash {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Password hash is required");
        }
    }
}


