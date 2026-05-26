package com.fluxusbackend.companyretail.domain.model.aggregates;

import com.fluxusbackend.shared.domain.model.aggregates.AuditableAggregateRoot;
import com.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "retail_company")
@AttributeOverride(name = "id", column = @Column(name = "retail_company_id", nullable = false, updatable = false))
public class RetailCompany extends AuditableAggregateRoot {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    protected RetailCompany() {
    }

    public RetailCompany(String name) {
        this.name = Objects.requireNonNull(name, "Company name is required");
    }

    public Optional<CompanyId> getCompanyId() {
        return getId() == null ? Optional.empty() : Optional.of(new CompanyId(getId()));
    }

    public Long getRetailCompanyId() {
        return getId();
    }

    public String getName() {
        return name;
    }

}

