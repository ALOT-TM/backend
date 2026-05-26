package com.fluxusbackend.beneficiary.domain.model.aggregates;

import com.fluxusbackend.shared.domain.model.aggregates.AuditableAggregateRoot;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "institution_type")
@AttributeOverride(name = "id", column = @Column(name = "institution_type_id", nullable = false, updatable = false))
public class InstitutionType extends AuditableAggregateRoot {

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    protected InstitutionType() {
    }

    public InstitutionType(String name) {
        this.name = Objects.requireNonNull(name, "Institution type name is required");
    }

    public Long getInstitutionTypeId() {
        return getId();
    }

    public String getName() {
        return name;
    }
}
