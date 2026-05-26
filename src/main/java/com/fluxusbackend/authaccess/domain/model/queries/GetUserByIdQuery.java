package com.fluxusbackend.authaccess.domain.model.queries;

import com.fluxusbackend.authaccess.domain.model.valueobjects.UserId;
import java.util.Objects;

public record GetUserByIdQuery(UserId userId) {
    public GetUserByIdQuery {
        Objects.requireNonNull(userId, "User id is required");
    }
}


