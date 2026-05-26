package com.fluxusbackend.authaccess.domain.model.queries;

import com.fluxusbackend.authaccess.domain.model.valueobjects.EmailAddress;
import java.util.Objects;

public record GetUserByEmailQuery(EmailAddress email) {
    public GetUserByEmailQuery {
        Objects.requireNonNull(email, "Email is required");
    }
}


