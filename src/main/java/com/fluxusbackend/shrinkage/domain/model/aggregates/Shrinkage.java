package com.fluxusbackend.shrinkage.domain.model.aggregates;

import com.fluxusbackend.companyretail.domain.model.aggregates.RetailCompany;
import com.fluxusbackend.companyretail.domain.model.aggregates.RetailCompanyHeadquarter;
import com.fluxusbackend.shared.domain.model.aggregates.AuditableAggregateRoot;
import com.fluxusbackend.shared.domain.model.aggregates.CompanyScoped;
import com.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
import com.fluxusbackend.shrinkage.domain.model.enums.ShrinkageStatus;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "shrinkage")
@AttributeOverride(name = "id", column = @Column(name = "shrinkage_id", nullable = false, updatable = false))
public class Shrinkage extends AuditableAggregateRoot implements CompanyScoped {

    @Embedded
    private CompanyId companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retail_company_id", insertable = false, updatable = false)
    private RetailCompany retailCompany;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "retail_company_headquarter_id", nullable = false)
    private RetailCompanyHeadquarter retailCompanyHeadquarter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shrinkage_reason_id", nullable = false)
    private ShrinkageReason shrinkageReason;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "specific_reason", length = 100)
    private String specificReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ShrinkageStatus status;

    @Column(name = "pickup_date")
    private LocalDate pickupDate;

    protected Shrinkage() {
    }

    public Shrinkage(
            RetailCompanyHeadquarter retailCompanyHeadquarter,
            Category category,
            ShrinkageReason shrinkageReason,
            String name,
            Integer quantity,
            LocalDate expirationDate,
            String specificReason,
            LocalDate pickupDate
    ) {
        this.retailCompanyHeadquarter = Objects.requireNonNull(retailCompanyHeadquarter, "Retail company headquarter is required");
        this.category = Objects.requireNonNull(category, "Category is required");
        this.shrinkageReason = Objects.requireNonNull(shrinkageReason, "Shrinkage reason is required");
        this.name = Objects.requireNonNull(name, "Name is required");
        this.quantity = Objects.requireNonNull(quantity, "Quantity is required");
        this.expirationDate = expirationDate;
        this.specificReason = specificReason;
        this.pickupDate = pickupDate;
        this.status = ShrinkageStatus.NONE;
    }

    public Long getShrinkageId() {
        return getId();
    }

    public Optional<RetailCompany> getRetailCompany() {
        return Optional.ofNullable(retailCompany);
    }

    public RetailCompanyHeadquarter getRetailCompanyHeadquarter() {
        return retailCompanyHeadquarter;
    }

    public Category getCategory() {
        return category;
    }

    public ShrinkageReason getShrinkageReason() {
        return shrinkageReason;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public String getSpecificReason() {
        return specificReason;
    }

    public ShrinkageStatus getStatus() {
        return status;
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    @Override
    public Optional<CompanyId> getCompanyId() {
        return Optional.ofNullable(companyId);
    }

    @Override
    public void setCompanyId(CompanyId companyId) {
        this.companyId = Objects.requireNonNull(companyId, "Company id is required");
    }

    public void markDonable() {
        if (status != ShrinkageStatus.NONE && status != ShrinkageStatus.NOT_DONABLE) {
            throw new IllegalStateException("Shrinkage must be NONE or NOT_DONABLE before marking donable");
        }
        status = ShrinkageStatus.DONABLE;
    }

    public void markInProcess() {
        markRequested();
    }

    public void markRequested() {
        if (status != ShrinkageStatus.DONABLE && status != ShrinkageStatus.REQUESTED) {
            throw new IllegalStateException("Shrinkage must be donable before marking requested");
        }
        status = ShrinkageStatus.REQUESTED;
    }

    public void markNotDonable() {
        if (status != ShrinkageStatus.NONE && status != ShrinkageStatus.DONABLE) {
            throw new IllegalStateException("Shrinkage must be NONE or DONABLE before marking not donable");
        }
        status = ShrinkageStatus.NOT_DONABLE;
    }

    public void markDonated() {
        if (status != ShrinkageStatus.DONABLE && status != ShrinkageStatus.REQUESTED) {
            throw new IllegalStateException("Shrinkage must be donable before marking donated");
        }
        status = ShrinkageStatus.DONATED;
    }
}


