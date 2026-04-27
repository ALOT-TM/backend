package com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.events;

import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.valueobjects.MermaId;
import java.time.Instant;
import java.util.Objects;

public final class MermaRegisteredEvent {

    private final MermaId mermaId;
    private final Instant occurredOn;

    public MermaRegisteredEvent(MermaId mermaId, Instant occurredOn) {
        this.mermaId = Objects.requireNonNull(mermaId, "Merma id is required");
        this.occurredOn = Objects.requireNonNull(occurredOn, "Occurred time is required");
    }

    public MermaId getMermaId() {
        return mermaId;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }
}

