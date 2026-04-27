package com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.queries;

import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.valueobjects.EmailAddress;
import java.util.Objects;

public record GetUserByEmailQuery(EmailAddress email) {
    public GetUserByEmailQuery {
        Objects.requireNonNull(email, "Email is required");
    }
}

