package com.fluxusbackend.shrinkage.domain.model.queries;

import com.fluxusbackend.shrinkage.domain.model.valueobjects.ShrinkageId;
import java.util.Objects;

public record GetShrinkageByIdQuery(ShrinkageId shrinkageId) {
    public GetShrinkageByIdQuery {
        Objects.requireNonNull(shrinkageId, "Shrinkage id is required");
    }
}


