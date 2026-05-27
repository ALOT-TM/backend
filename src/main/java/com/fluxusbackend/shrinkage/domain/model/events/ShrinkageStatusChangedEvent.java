package com.fluxusbackend.shrinkage.domain.model.events;

import com.fluxusbackend.shrinkage.domain.model.enums.ShrinkageStatus;
import com.fluxusbackend.shrinkage.domain.model.valueobjects.ShrinkageId;
import java.time.Instant;

public record ShrinkageStatusChangedEvent(
    ShrinkageId shrinkageId,
    ShrinkageStatus oldStatus,
    ShrinkageStatus newStatus,
    Instant occurredOn
) {}
