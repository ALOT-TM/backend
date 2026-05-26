package com.fluxusbackend.shrinkage.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record CategoryName(@Column(name = "category_name", nullable = false, length = 120) String value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public CategoryName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }
    }

    @JsonValue
    public String value() {
        return value;
    }
}


