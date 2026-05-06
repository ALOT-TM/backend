package com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands;

import jakarta.validation.constraints.NotNull;

public record CreateDonationRequestCommand(
        @NotNull Long mermaId,
        @NotNull Long beneficiaryId,
        String notes
) {
}
