package com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.commands;

import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.valueobjects.MermaId;
import java.util.Objects;

public record MarkMermaDonatedCommand(MermaId mermaId) {
    public MarkMermaDonatedCommand {
        Objects.requireNonNull(mermaId, "Merma id is required");
    }
}

