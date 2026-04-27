package com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;

@Embeddable
public record EmailAddress(@Column(name = "email", nullable = false, length = 150) String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public EmailAddress {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Email format is invalid");
        }
    }

    @JsonValue
    public String value() {
        return value;
    }
}

