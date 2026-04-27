package com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.aggregates;

import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.enums.BeneficiaryStatus;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.enums.BeneficiaryType;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.valueobjects.AcceptedProduct;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.valueobjects.Address;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.valueobjects.BeneficiaryId;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.valueobjects.BeneficiaryName;
import com.fluxusbackend.fluxusbackend.shared.domain.model.aggregates.AuditableAggregateRoot;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "beneficiaries")
public class Beneficiary extends AuditableAggregateRoot {

    @Embedded
    private BeneficiaryName name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private BeneficiaryType type;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private BeneficiaryStatus status;

    @ElementCollection
    @CollectionTable(name = "beneficiary_accepted_products", joinColumns = @JoinColumn(name = "beneficiary_id"))
    private Set<AcceptedProduct> acceptedProducts = new HashSet<>();

    protected Beneficiary() {
    }

    public Beneficiary(
            BeneficiaryName name,
            BeneficiaryType type,
            Address address,
            Set<AcceptedProduct> acceptedProducts
    ) {
        this.name = Objects.requireNonNull(name, "Name is required");
        this.type = Objects.requireNonNull(type, "Type is required");
        this.address = Objects.requireNonNull(address, "Address is required");
        this.acceptedProducts = new HashSet<>(Objects.requireNonNull(acceptedProducts, "Accepted products are required"));
        if (this.acceptedProducts.isEmpty()) {
            throw new IllegalArgumentException("At least one accepted product is required");
        }
        this.status = BeneficiaryStatus.ACTIVE;
    }

    public BeneficiaryId getBeneficiaryId() {
        return new BeneficiaryId(getId());
    }

    public BeneficiaryName getName() {
        return name;
    }

    public BeneficiaryType getType() {
        return type;
    }

    public Address getAddress() {
        return address;
    }

    public BeneficiaryStatus getStatus() {
        return status;
    }

    public Set<AcceptedProduct> getAcceptedProducts() {
        return new HashSet<>(acceptedProducts);
    }

    public void updateInfo(BeneficiaryName name, BeneficiaryType type, Address address, Set<AcceptedProduct> products) {
        this.name = Objects.requireNonNull(name, "Name is required");
        this.type = Objects.requireNonNull(type, "Type is required");
        this.address = Objects.requireNonNull(address, "Address is required");
        var updated = new HashSet<>(Objects.requireNonNull(products, "Accepted products are required"));
        if (updated.isEmpty()) {
            throw new IllegalArgumentException("At least one accepted product is required");
        }
        this.acceptedProducts = updated;
    }

    public void activate() {
        status = BeneficiaryStatus.ACTIVE;
    }

    public void deactivate() {
        status = BeneficiaryStatus.INACTIVE;
    }
}

