package com.fluxusbackend.shared.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record CompanyId(@Column(name = "retail_company_id") Long value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public CompanyId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Company id must be positive");
        }
    }

    @JsonValue
    public Long value() {
        return value;
    }
}
