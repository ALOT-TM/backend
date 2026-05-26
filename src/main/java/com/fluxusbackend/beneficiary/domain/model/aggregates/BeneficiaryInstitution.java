package com.fluxusbackend.beneficiary.domain.model.aggregates;

import com.fluxusbackend.shared.domain.model.aggregates.AuditableAggregateRoot;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "beneficiary_institution")
@AttributeOverride(name = "id", column = @Column(name = "beneficiary_institution_id", nullable = false, updatable = false))
public class BeneficiaryInstitution extends AuditableAggregateRoot {

    @ManyToOne(optional = false)
    @JoinColumn(name = "institution_type_id", nullable = false)
    private InstitutionType institutionType;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    protected BeneficiaryInstitution() {
    }

    public BeneficiaryInstitution(InstitutionType institutionType, String name) {
        this.institutionType = Objects.requireNonNull(institutionType, "Institution type is required");
        this.name = Objects.requireNonNull(name, "Name is required");
    }

    public Long getBeneficiaryInstitutionId() {
        return getId();
    }

    public InstitutionType getInstitutionType() {
        return institutionType;
    }

    public String getName() {
        return name;
    }

    public void updateInfo(InstitutionType institutionType, String name) {
        this.institutionType = Objects.requireNonNull(institutionType, "Institution type is required");
        this.name = Objects.requireNonNull(name, "Name is required");
    }
}


