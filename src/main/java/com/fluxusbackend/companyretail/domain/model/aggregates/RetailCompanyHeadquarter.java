package com.fluxusbackend.companyretail.domain.model.aggregates;

import com.fluxusbackend.location.domain.model.aggregates.Address;
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
@Table(name = "retail_company_headquarter")
@AttributeOverride(name = "id", column = @Column(name = "retail_company_headquarter_id", nullable = false, updatable = false))
public class RetailCompanyHeadquarter extends AuditableAggregateRoot {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "retail_company_id", nullable = false)
    private RetailCompany retailCompany;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    protected RetailCompanyHeadquarter() {
    }

    public RetailCompanyHeadquarter(RetailCompany retailCompany, String description, Address address) {
        this.retailCompany = Objects.requireNonNull(retailCompany, "Retail company is required");
        this.description = Objects.requireNonNull(description, "Description is required");
        this.address = Objects.requireNonNull(address, "Address is required");
    }

    public Long getRetailCompanyHeadquarterId() {
        return getId();
    }

    public RetailCompany getRetailCompany() {
        return retailCompany;
    }

    public String getDescription() {
        return description;
    }

    public Address getAddress() {
        return address;
    }
}
