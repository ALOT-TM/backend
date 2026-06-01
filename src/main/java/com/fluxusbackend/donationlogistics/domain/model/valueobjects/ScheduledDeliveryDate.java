package com.fluxusbackend.donationlogistics.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
@io.swagger.v3.oas.annotations.media.Schema(type = "string", format = "date", example = "2026-06-05")
public record ScheduledDeliveryDate(@Column(name = "scheduled_delivery_date", nullable = false) LocalDate value) {

    public ScheduledDeliveryDate {
        if (value == null) {
            throw new IllegalArgumentException("Scheduled delivery date is required");
        }
    }

    @JsonCreator
    public static ScheduledDeliveryDate fromValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate localDate) {
            return new ScheduledDeliveryDate(localDate);
        }
        if (value instanceof String str) {
            return new ScheduledDeliveryDate(LocalDate.parse(str));
        }
        if (value instanceof java.util.Map<?, ?> map) {
            Object val = map.get("value");
            if (val instanceof LocalDate localDate) {
                return new ScheduledDeliveryDate(localDate);
            }
            if (val instanceof String str) {
                return new ScheduledDeliveryDate(LocalDate.parse(str));
            }
        }
        throw new IllegalArgumentException("Cannot deserialize ScheduledDeliveryDate from: " + value);
    }

    @JsonValue
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public LocalDate value() {
        return value;
    }
}


