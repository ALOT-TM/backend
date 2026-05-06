package com.fluxusbackend.fluxusbackend.companymanagement.domain.model.aggregates;

import com.fluxusbackend.fluxusbackend.shared.domain.model.aggregates.AuditableAggregateRoot;
import com.fluxusbackend.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "companies")
public class Company extends AuditableAggregateRoot {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "headquarters", length = 250)
    private String headquarters;

    protected Company() {
    }

    public Company(String name, String headquarters) {
        this.name = Objects.requireNonNull(name, "Company name is required");
        this.headquarters = headquarters;
    }

    public Optional<CompanyId> getCompanyId() {
        return getId() == null ? Optional.empty() : Optional.of(new CompanyId(getId()));
    }

    public String getName() {
        return name;
    }

    public String getHeadquarters() {
        return headquarters;
    }
}
