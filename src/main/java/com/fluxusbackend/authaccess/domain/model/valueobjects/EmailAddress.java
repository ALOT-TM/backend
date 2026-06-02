package com.fluxusbackend.authaccess.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;

@Embeddable
@io.swagger.v3.oas.annotations.media.Schema(type = "string", example = "raul@gmail.com")
public record EmailAddress(@Column(name = "email", nullable = false, length = 100) String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public EmailAddress {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Email format is invalid");
        }
    }

    @JsonCreator
    public static EmailAddress fromValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String str) {
            return new EmailAddress(str);
        }
        if (value instanceof java.util.Map<?, ?> map) {
            Object val = map.get("value");
            if (val instanceof String str) {
                return new EmailAddress(str);
            }
        }
        throw new IllegalArgumentException("Cannot deserialize EmailAddress from: " + value);
    }

    @JsonValue
    public String value() {
        return value;
    }
}


