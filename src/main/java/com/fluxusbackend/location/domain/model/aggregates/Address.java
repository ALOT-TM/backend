package com.fluxusbackend.location.domain.model.aggregates;

import com.fluxusbackend.shared.domain.model.aggregates.AuditableAggregateRoot;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "address")
@AttributeOverride(name = "id", column = @Column(name = "address_id", nullable = false, updatable = false))
public class Address extends AuditableAggregateRoot {

    @Column(name = "street1", nullable = false, length = 100)
    private String street1;

    @Column(name = "street2", length = 100)
    private String street2;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state_province", nullable = false, length = 100)
    private String stateProvince;

    @Column(name = "postal_code", nullable = false, length = 100)
    private String postalCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    protected Address() {
    }

    public Address(
            String street1,
            String street2,
            String city,
            String stateProvince,
            String postalCode,
            Country country
    ) {
        this.street1 = Objects.requireNonNull(street1, "Street1 is required");
        this.street2 = street2;
        this.city = Objects.requireNonNull(city, "City is required");
        this.stateProvince = Objects.requireNonNull(stateProvince, "State/Province is required");
        this.postalCode = Objects.requireNonNull(postalCode, "Postal code is required");
        this.country = Objects.requireNonNull(country, "Country is required");
    }

    public Long getAddressId() {
        return getId();
    }

    public String getStreet1() {
        return street1;
    }

    public String getStreet2() {
        return street2;
    }

    public String getCity() {
        return city;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Country getCountry() {
        return country;
    }

    public void updateStreet1(String street1) {
        this.street1 = Objects.requireNonNull(street1, "Street1 is required");
    }
}
