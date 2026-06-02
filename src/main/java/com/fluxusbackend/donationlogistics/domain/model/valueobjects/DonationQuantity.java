package com.fluxusbackend.donationlogistics.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@io.swagger.v3.oas.annotations.media.Schema(type = "integer", example = "5")
public record DonationQuantity(@Column(name = "donation_quantity", nullable = false) int amount) {

    public DonationQuantity {
        if (amount <= 0) {
            throw new IllegalArgumentException("Donation quantity must be greater than zero");
        }
    }

    @JsonCreator
    public static DonationQuantity fromValue(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Donation quantity is required");
        }
        if (value instanceof Number number) {
            return new DonationQuantity(number.intValue());
        }
        if (value instanceof String str) {
            return new DonationQuantity(Integer.parseInt(str));
        }
        if (value instanceof java.util.Map<?, ?> map) {
            Object val = map.get("amount");
            if (val instanceof Number number) {
                return new DonationQuantity(number.intValue());
            } else if (val instanceof String str) {
                return new DonationQuantity(Integer.parseInt(str));
            }
        }
        throw new IllegalArgumentException("Cannot deserialize DonationQuantity from: " + value);
    }

    @JsonValue
    public int amount() {
        return amount;
    }
}


