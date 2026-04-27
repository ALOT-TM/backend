package com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.queries;

import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.valueobjects.MermaId;
import java.util.Objects;

public record GetMermaByIdQuery(MermaId mermaId) {
    public GetMermaByIdQuery {
        Objects.requireNonNull(mermaId, "Merma id is required");
    }
}

