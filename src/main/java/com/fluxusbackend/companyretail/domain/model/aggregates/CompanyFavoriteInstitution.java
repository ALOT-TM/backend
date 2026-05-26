package com.fluxusbackend.companyretail.domain.model.aggregates;

import com.fluxusbackend.beneficiary.domain.model.aggregates.BeneficiaryInstitution;
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
@Table(name = "company_favorite_institution")
@AttributeOverride(name = "id", column = @Column(name = "company_favorite_institution_id", nullable = false, updatable = false))
public class CompanyFavoriteInstitution extends AuditableAggregateRoot {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "retail_company_id", nullable = false)
    private RetailCompany retailCompany;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "beneficiary_institution_id", nullable = false)
    private BeneficiaryInstitution beneficiaryInstitution;

    protected CompanyFavoriteInstitution() {
    }

    public CompanyFavoriteInstitution(RetailCompany retailCompany, BeneficiaryInstitution beneficiaryInstitution) {
        this.retailCompany = Objects.requireNonNull(retailCompany, "Retail company is required");
        this.beneficiaryInstitution = Objects.requireNonNull(beneficiaryInstitution, "Beneficiary institution is required");
    }

    public Long getCompanyFavoriteInstitutionId() {
        return getId();
    }

    public RetailCompany getRetailCompany() {
        return retailCompany;
    }

    public BeneficiaryInstitution getBeneficiaryInstitution() {
        return beneficiaryInstitution;
    }
}
