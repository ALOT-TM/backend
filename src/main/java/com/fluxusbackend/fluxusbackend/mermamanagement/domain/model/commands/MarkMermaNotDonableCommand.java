package com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.commands;

import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.valueobjects.MermaId;
import java.util.Objects;

public record MarkMermaNotDonableCommand(MermaId mermaId) {
    public MarkMermaNotDonableCommand {
        Objects.requireNonNull(mermaId, "Merma id is required");
    }
}

