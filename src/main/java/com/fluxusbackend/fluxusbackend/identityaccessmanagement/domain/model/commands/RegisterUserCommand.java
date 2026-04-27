package com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.commands;

import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.enums.UserRole;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.valueobjects.EmailAddress;
import java.util.Objects;

public record RegisterUserCommand(EmailAddress email, String rawPassword, UserRole role) {
    public RegisterUserCommand {
        Objects.requireNonNull(email, "Email is required");
        Objects.requireNonNull(rawPassword, "Password is required");
        Objects.requireNonNull(role, "Role is required");
        if (rawPassword.isBlank() || rawPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }
}

