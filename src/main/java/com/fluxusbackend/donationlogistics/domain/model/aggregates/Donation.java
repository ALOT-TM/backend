package com.fluxusbackend.donationlogistics.domain.model.aggregates;

import com.fluxusbackend.donationlogistics.domain.model.enums.DonationItemStatus;
import com.fluxusbackend.donationlogistics.domain.model.enums.DonationStatus;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.BeneficiaryReferenceId;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.DeliveryDate;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.DonationId;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.DonationQuantity;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.ShrinkageReferenceId;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.ReceptionDate;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.ScheduledDeliveryDate;
import com.fluxusbackend.shared.domain.model.aggregates.AuditableAggregateRoot;
import com.fluxusbackend.shared.domain.model.aggregates.CompanyScoped;
import com.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "donations")
public class Donation extends AuditableAggregateRoot implements CompanyScoped {

    @OneToMany(mappedBy = "donation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DonationItem> items = new ArrayList<>();

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "beneficiary_institution_id", nullable = false))
    private BeneficiaryReferenceId beneficiaryReferenceId;

    @Embedded
    private DonationQuantity quantity;

    @Embedded
    private ScheduledDeliveryDate scheduledDeliveryDate;

    @Embedded
    private DeliveryDate deliveryDate;

    @Embedded
    private ReceptionDate receptionDate;

    @Column(name = "reception_comment", length = 250)
    private String receptionComment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private DonationStatus status;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Embedded
    private CompanyId companyId;

    protected Donation() {
    }

    public Donation(
            ShrinkageReferenceId shrinkageReferenceId,
            BeneficiaryReferenceId beneficiaryReferenceId,
            DonationQuantity quantity,
            ScheduledDeliveryDate scheduledDeliveryDate
    ) {
        this.beneficiaryReferenceId = Objects.requireNonNull(beneficiaryReferenceId, "Beneficiary reference id is required");
        this.quantity = Objects.requireNonNull(quantity, "Donation quantity is required");
        this.scheduledDeliveryDate = Objects.requireNonNull(scheduledDeliveryDate, "Scheduled delivery date is required");
        this.status = DonationStatus.ASSIGNED;
        this.items.add(new DonationItem(this, shrinkageReferenceId, DonationItemStatus.ASSIGNED));
    }

    public DonationId getDonationId() {
        return new DonationId(getId());
    }

    public List<DonationItem> getItems() {
        return items;
    }

    public ShrinkageReferenceId getShrinkageReferenceId() {
        return items.isEmpty() ? null : items.get(0).getShrinkageReferenceId();
    }

    public BeneficiaryReferenceId getBeneficiaryReferenceId() {
        return beneficiaryReferenceId;
    }

    public DonationQuantity getQuantity() {
        return quantity;
    }

    public ScheduledDeliveryDate getScheduledDeliveryDate() {
        return scheduledDeliveryDate;
    }

    public Optional<DeliveryDate> getDeliveryDate() {
        return Optional.ofNullable(deliveryDate);
    }

    public Optional<ReceptionDate> getReceptionDate() {
        return Optional.ofNullable(receptionDate);
    }

    public Optional<String> getReceptionComment() {
        return Optional.ofNullable(receptionComment);
    }

    public DonationStatus getStatus() {
        return status;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public java.util.Optional<CompanyId> getCompanyId() {
        return Optional.ofNullable(companyId);
    }

    public void setCompanyId(CompanyId companyId) {
        this.companyId = companyId;
    }

    public void markDelivered(DeliveryDate deliveryDate) {
        if (status != DonationStatus.ASSIGNED) {
            throw new IllegalStateException("Donation must be assigned before delivery");
        }
        this.deliveryDate = Objects.requireNonNull(deliveryDate, "Delivery date is required");
        this.status = DonationStatus.DELIVERED;
        for (var item : items) {
            item.setStatus(DonationItemStatus.DELIVERED);
        }
    }

    public void confirmReception(ReceptionDate receptionDate, Optional<String> comment) {
        if (status != DonationStatus.DELIVERED) {
            throw new IllegalStateException("Donation must be delivered before confirmation");
        }
        this.receptionDate = Objects.requireNonNull(receptionDate, "Reception date is required");
        this.receptionComment = comment == null ? null : comment.orElse(null);
        this.status = DonationStatus.CONFIRMED;
        this.completedAt = Instant.now();
        for (var item : items) {
            item.setStatus(DonationItemStatus.CONFIRMED);
        }
    }
}


