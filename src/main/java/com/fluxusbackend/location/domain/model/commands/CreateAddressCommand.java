package com.fluxusbackend.location.domain.model.commands;

import java.util.Objects;

public record CreateAddressCommand(
        String street1,
        String street2,
        String city,
        String stateProvince,
        String postalCode,
        Long countryId
) {
    public CreateAddressCommand {
        Objects.requireNonNull(street1, "Street1 is required");
        Objects.requireNonNull(city, "City is required");
        Objects.requireNonNull(stateProvince, "State/Province is required");
        Objects.requireNonNull(postalCode, "Postal code is required");
        Objects.requireNonNull(countryId, "Country id is required");
        if (street1.isBlank()) {
            throw new IllegalArgumentException("Street1 is required");
        }
        if (city.isBlank()) {
            throw new IllegalArgumentException("City is required");
        }
        if (stateProvince.isBlank()) {
            throw new IllegalArgumentException("State/Province is required");
        }
        if (postalCode.isBlank()) {
            throw new IllegalArgumentException("Postal code is required");
        }
        if (countryId <= 0) {
            throw new IllegalArgumentException("Country id must be positive");
        }
    }
}

