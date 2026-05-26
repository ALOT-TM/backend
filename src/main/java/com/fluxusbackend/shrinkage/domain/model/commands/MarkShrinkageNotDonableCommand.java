package com.fluxusbackend.shrinkage.domain.model.commands;

import com.fluxusbackend.shrinkage.domain.model.valueobjects.ShrinkageId;
import java.util.Objects;

public record MarkShrinkageNotDonableCommand(ShrinkageId shrinkageId) {
    public MarkShrinkageNotDonableCommand {
        Objects.requireNonNull(shrinkageId, "Shrinkage id is required");
    }
}


