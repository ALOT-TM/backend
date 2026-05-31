package com.fluxusbackend.authaccess.domain.model.commands;

import java.util.Objects;

public record UpdateProfileCommand(Long userId, String username, String email) {
    public UpdateProfileCommand {
        Objects.requireNonNull(userId, "User id is required");
        Objects.requireNonNull(username, "Username is required");
        Objects.requireNonNull(email, "Email is required");
        if (username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
    }
}
