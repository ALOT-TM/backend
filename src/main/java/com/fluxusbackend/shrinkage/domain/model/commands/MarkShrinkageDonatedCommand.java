package com.fluxusbackend.shrinkage.domain.model.commands;

import com.fluxusbackend.shrinkage.domain.model.valueobjects.ShrinkageId;
import java.util.Objects;

public record MarkShrinkageDonatedCommand(ShrinkageId shrinkageId) {
    public MarkShrinkageDonatedCommand {
        Objects.requireNonNull(shrinkageId, "Shrinkage id is required");
    }
}


