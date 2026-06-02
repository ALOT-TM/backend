package com.fluxusbackend.beneficiary.domain.model.aggregates;

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
@Table(name = "beneficiary_institution_headquarter")
@AttributeOverride(name = "id", column = @Column(name = "beneficiary_institution_headquarter_id", nullable = false, updatable = false))
public class BeneficiaryInstitutionHeadquarter extends AuditableAggregateRoot {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "beneficiary_institution_id", nullable = false)
    private BeneficiaryInstitution beneficiaryInstitution;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    protected BeneficiaryInstitutionHeadquarter() {
    }

    public BeneficiaryInstitutionHeadquarter(
            BeneficiaryInstitution beneficiaryInstitution,
            String description,
            Address address
    ) {
        this.beneficiaryInstitution = Objects.requireNonNull(beneficiaryInstitution, "Beneficiary institution is required");
        this.description = Objects.requireNonNull(description, "Description is required");
        this.address = Objects.requireNonNull(address, "Address is required");
    }

    public Long getBeneficiaryInstitutionHeadquarterId() {
        return getId();
    }

    public BeneficiaryInstitution getBeneficiaryInstitution() {
        return beneficiaryInstitution;
    }

    public String getDescription() {
        return description;
    }

    public Address getAddress() {
        return address;
    }

    public void updateDescription(String description) {
        this.description = Objects.requireNonNull(description, "Description is required");
    }
}
