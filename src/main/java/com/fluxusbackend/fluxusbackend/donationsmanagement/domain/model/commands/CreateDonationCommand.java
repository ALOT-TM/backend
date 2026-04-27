package com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.BeneficiaryReferenceId;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.DonationQuantity;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.MermaReferenceId;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.ScheduledDeliveryDate;
import java.util.Objects;

public record CreateDonationCommand(
        MermaReferenceId mermaReferenceId,
        BeneficiaryReferenceId beneficiaryReferenceId,
        DonationQuantity quantity,
        ScheduledDeliveryDate scheduledDeliveryDate
) {
    public CreateDonationCommand {
        Objects.requireNonNull(mermaReferenceId, "Merma reference id is required");
        Objects.requireNonNull(beneficiaryReferenceId, "Beneficiary reference id is required");
        Objects.requireNonNull(quantity, "Donation quantity is required");
        Objects.requireNonNull(scheduledDeliveryDate, "Scheduled delivery date is required");
    }
}

