package com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
public record ReceptionDate(@Column(name = "reception_date") LocalDate value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public ReceptionDate {
        if (value == null) {
            throw new IllegalArgumentException("Reception date is required");
        }
    }

    @JsonValue
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public LocalDate value() {
        return value;
    }
}

