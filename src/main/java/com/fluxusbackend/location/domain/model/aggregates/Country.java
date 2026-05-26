package com.fluxusbackend.location.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    protected Country() {
    }

    public Country(String name) {
        this.name = Objects.requireNonNull(name, "Country name is required");
    }

    public Long getCountryId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
