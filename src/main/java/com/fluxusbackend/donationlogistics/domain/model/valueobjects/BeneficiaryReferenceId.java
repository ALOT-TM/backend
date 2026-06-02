package com.fluxusbackend.donationlogistics.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@io.swagger.v3.oas.annotations.media.Schema(type = "integer", format = "int64", example = "1")
public record BeneficiaryReferenceId(@Column(name = "beneficiary_id", nullable = false) Long value) {

    public BeneficiaryReferenceId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Beneficiary reference id must be positive");
        }
    }

    @JsonCreator
    public static BeneficiaryReferenceId fromValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return new BeneficiaryReferenceId(number.longValue());
        }
        if (value instanceof String str) {
            return new BeneficiaryReferenceId(Long.parseLong(str));
        }
        if (value instanceof java.util.Map<?, ?> map) {
            Object val = map.get("value");
            if (val instanceof Number number) {
                return new BeneficiaryReferenceId(number.longValue());
            } else if (val instanceof String str) {
                return new BeneficiaryReferenceId(Long.parseLong(str));
            }
        }
        throw new IllegalArgumentException("Cannot deserialize BeneficiaryReferenceId from: " + value);
    }

    @JsonValue
    public Long value() {
        return value;
    }
}


