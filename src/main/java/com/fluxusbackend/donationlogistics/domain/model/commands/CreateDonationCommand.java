package com.fluxusbackend.donationlogistics.domain.model.commands;

import com.fluxusbackend.donationlogistics.domain.model.valueobjects.BeneficiaryReferenceId;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.DonationQuantity;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.ShrinkageReferenceId;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.ScheduledDeliveryDate;
import java.util.Objects;

public record CreateDonationCommand(
        ShrinkageReferenceId shrinkageReferenceId,
        BeneficiaryReferenceId beneficiaryReferenceId,
        DonationQuantity quantity,
        ScheduledDeliveryDate scheduledDeliveryDate
) {
    public CreateDonationCommand {
        Objects.requireNonNull(shrinkageReferenceId, "Shrinkage reference id is required");
        Objects.requireNonNull(beneficiaryReferenceId, "Beneficiary reference id is required");
        Objects.requireNonNull(quantity, "Donation quantity is required");
        Objects.requireNonNull(scheduledDeliveryDate, "Scheduled delivery date is required");
    }
}


