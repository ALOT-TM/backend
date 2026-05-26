package com.fluxusbackend.shrinkage.domain.model.queries;

import com.fluxusbackend.shrinkage.domain.model.enums.ShrinkageStatus;
import java.util.Objects;

public record ListShrinkagesByStatusQuery(ShrinkageStatus status) {
    public ListShrinkagesByStatusQuery {
        Objects.requireNonNull(status, "Status is required");
    }
}


