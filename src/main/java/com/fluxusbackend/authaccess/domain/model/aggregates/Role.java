package com.fluxusbackend.authaccess.domain.model.aggregates;

import com.fluxusbackend.companyretail.domain.model.aggregates.RetailCompany;
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
@Table(name = "role")
@AttributeOverride(name = "id", column = @Column(name = "role_id", nullable = false, updatable = false))
public class Role extends AuditableAggregateRoot {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "retail_company_id", nullable = false)
    private RetailCompany retailCompany;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    protected Role() {
    }

    public Role(RetailCompany retailCompany, String name) {
        this.retailCompany = Objects.requireNonNull(retailCompany, "Retail company is required");
        this.name = Objects.requireNonNull(name, "Role name is required");
    }

    public Long getRoleId() {
        return getId();
    }

    public RetailCompany getRetailCompany() {
        return retailCompany;
    }

    public String getName() {
        return name;
    }
}
