package com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.queries;

import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.enums.MermaStatus;
import java.util.Objects;

public record ListMermasByStatusQuery(MermaStatus status) {
    public ListMermasByStatusQuery {
        Objects.requireNonNull(status, "Status is required");
    }
}

