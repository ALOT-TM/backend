package com.fluxusbackend.shrinkage.domain.model.commands;

import com.fluxusbackend.shrinkage.domain.model.valueobjects.ShrinkageId;
import java.util.Objects;

public record MarkShrinkageInProcessCommand(ShrinkageId shrinkageId) {
    public MarkShrinkageInProcessCommand {
        Objects.requireNonNull(shrinkageId, "Shrinkage id is required");
    }
}
