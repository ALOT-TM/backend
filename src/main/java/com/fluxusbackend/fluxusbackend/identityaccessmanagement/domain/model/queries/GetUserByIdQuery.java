package com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.queries;

import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.valueobjects.UserId;
import java.util.Objects;

public record GetUserByIdQuery(UserId userId) {
    public GetUserByIdQuery {
        Objects.requireNonNull(userId, "User id is required");
    }
}

