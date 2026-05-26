package com.fluxusbackend.beneficiary.domain.model.events;

import com.fluxusbackend.beneficiary.domain.model.valueobjects.BeneficiaryId;
import java.time.Instant;
import java.util.Objects;

public final class BeneficiaryRegisteredEvent {

    private final BeneficiaryId beneficiaryId;
    private final Instant occurredOn;

    public BeneficiaryRegisteredEvent(BeneficiaryId beneficiaryId, Instant occurredOn) {
        this.beneficiaryId = Objects.requireNonNull(beneficiaryId, "Beneficiary id is required");
        this.occurredOn = Objects.requireNonNull(occurredOn, "Occurred time is required");
    }

    public BeneficiaryId getBeneficiaryId() {
        return beneficiaryId;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }
}


