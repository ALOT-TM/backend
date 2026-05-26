package com.fluxusbackend.shrinkage.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
public record ExpirationDate(@Column(name = "expiration_date", nullable = false) LocalDate value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public ExpirationDate {
        if (value == null) {
            throw new IllegalArgumentException("Expiration date is required");
        }
    }

    @JsonValue
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public LocalDate value() {
        return value;
    }
}


