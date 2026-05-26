package com.fluxusbackend.shrinkage.domain.model.events;

import com.fluxusbackend.shrinkage.domain.model.valueobjects.ShrinkageId;

import java.time.Instant;
import java.util.Objects;

public final class MermaRegisteredEvent {

    private final ShrinkageId mermaId;
    private final Instant occurredOn;

    public MermaRegisteredEvent(ShrinkageId mermaId, Instant occurredOn) {
        this.mermaId = Objects.requireNonNull(mermaId, "Merma id is required");
        this.occurredOn = Objects.requireNonNull(occurredOn, "Occurred time is required");
    }

    public ShrinkageId getMermaId() {
        return mermaId;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }
}


