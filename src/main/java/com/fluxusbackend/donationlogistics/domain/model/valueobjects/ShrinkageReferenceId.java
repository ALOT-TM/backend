package com.fluxusbackend.donationlogistics.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@io.swagger.v3.oas.annotations.media.Schema(type = "integer", format = "int64", example = "1")
public record ShrinkageReferenceId(@Column(name = "shrinkage_id", nullable = false) Long value) {

    public ShrinkageReferenceId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Shrinkage reference id must be positive");
        }
    }

    @JsonCreator
    public static ShrinkageReferenceId fromValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return new ShrinkageReferenceId(number.longValue());
        }
        if (value instanceof String str) {
            return new ShrinkageReferenceId(Long.parseLong(str));
        }
        if (value instanceof java.util.Map<?, ?> map) {
            Object val = map.get("value");
            if (val instanceof Number number) {
                return new ShrinkageReferenceId(number.longValue());
            } else if (val instanceof String str) {
                return new ShrinkageReferenceId(Long.parseLong(str));
            }
        }
        throw new IllegalArgumentException("Cannot deserialize ShrinkageReferenceId from: " + value);
    }

    @JsonValue
    public Long value() {
        return value;
    }
}
