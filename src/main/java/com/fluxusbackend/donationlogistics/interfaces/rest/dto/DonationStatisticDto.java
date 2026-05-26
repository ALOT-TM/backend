package com.fluxusbackend.donationlogistics.interfaces.rest.dto;

public record DonationStatisticDto(
    Long beneficiaryId,
    String beneficiaryName,
    long totalDonations,
    long totalQuantityDonated
) {
}

