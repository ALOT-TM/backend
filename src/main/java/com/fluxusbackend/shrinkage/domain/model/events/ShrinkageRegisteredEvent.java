package com.fluxusbackend.shrinkage.domain.model.events;

import com.fluxusbackend.shrinkage.domain.model.valueobjects.ShrinkageId;

import java.time.Instant;
import java.util.Objects;

public final class ShrinkageRegisteredEvent {

    private final ShrinkageId shrinkageId;
    private final Instant occurredOn;

    public ShrinkageRegisteredEvent(ShrinkageId shrinkageId, Instant occurredOn) {
        this.shrinkageId = Objects.requireNonNull(shrinkageId, "Shrinkage id is required");
        this.occurredOn = Objects.requireNonNull(occurredOn, "Occurred time is required");
    }

    public ShrinkageId getShrinkageId() {
        return shrinkageId;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }
}
