package com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.aggregates;

import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.enums.MermaReason;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.enums.MermaStatus;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.valueobjects.CategoryName;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.valueobjects.ExpirationDate;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.valueobjects.MermaId;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.valueobjects.ProductName;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.valueobjects.Quantity;
import com.fluxusbackend.fluxusbackend.shared.domain.model.aggregates.AuditableAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "mermas")
public class Merma extends AuditableAggregateRoot {

    @Embedded
    private ProductName productName;

    @Embedded
    private CategoryName categoryName;

    @Embedded
    private Quantity quantity;

    @Embedded
    private ExpirationDate expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 30)
    private MermaReason reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private MermaStatus status;

    protected Merma() {
    }

    public Merma(
            ProductName productName,
            CategoryName categoryName,
            Quantity quantity,
            ExpirationDate expirationDate,
            MermaReason reason
    ) {
        this.productName = Objects.requireNonNull(productName, "Product name is required");
        this.categoryName = Objects.requireNonNull(categoryName, "Category name is required");
        this.quantity = Objects.requireNonNull(quantity, "Quantity is required");
        this.expirationDate = Objects.requireNonNull(expirationDate, "Expiration date is required");
        this.reason = Objects.requireNonNull(reason, "Reason is required");
        this.status = MermaStatus.REGISTERED;
    }

    public MermaId getMermaId() {
        return new MermaId(getId());
    }

    public ProductName getProductName() {
        return productName;
    }

    public CategoryName getCategoryName() {
        return categoryName;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public ExpirationDate getExpirationDate() {
        return expirationDate;
    }

    public MermaReason getReason() {
        return reason;
    }

    public MermaStatus getStatus() {
        return status;
    }

    public void markDonable() {
        if (status != MermaStatus.REGISTERED) {
            throw new IllegalStateException("Merma must be registered before marking donable");
        }
        status = MermaStatus.DONABLE;
    }

    public void markNotDonable() {
        if (status != MermaStatus.REGISTERED) {
            throw new IllegalStateException("Merma must be registered before marking not donable");
        }
        status = MermaStatus.NOT_DONABLE;
    }

    public void markDonated() {
        if (status != MermaStatus.DONABLE) {
            throw new IllegalStateException("Merma must be donable before marking donated");
        }
        status = MermaStatus.DONATED;
    }
}

