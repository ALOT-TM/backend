package com.fluxusbackend.authaccess.domain.model.queries;

import com.fluxusbackend.authaccess.domain.model.valueobjects.EmailAddress;
import java.util.Objects;

public record LoginUserQuery(EmailAddress email, String rawPassword) {
    public LoginUserQuery {
        Objects.requireNonNull(email, "Email is required");
        Objects.requireNonNull(rawPassword, "Password is required");
        if (rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
    }
}


