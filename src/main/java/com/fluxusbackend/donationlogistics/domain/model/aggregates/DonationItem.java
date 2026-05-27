package com.fluxusbackend.donationlogistics.domain.model.aggregates;

import com.fluxusbackend.donationlogistics.domain.model.enums.DonationItemStatus;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.ShrinkageReferenceId;
import com.fluxusbackend.shared.domain.model.aggregates.AuditableAggregateRoot;
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
import java.util.Objects;

@Entity
@Table(name = "donation_items")
@AttributeOverride(name = "id", column = @Column(name = "donation_item_id", nullable = false, updatable = false))
public class DonationItem extends AuditableAggregateRoot {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id", nullable = false)
    private Donation donation;

    @Embedded
    private ShrinkageReferenceId shrinkageReferenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private DonationItemStatus status;

    protected DonationItem() {
    }

    public DonationItem(Donation donation, ShrinkageReferenceId shrinkageReferenceId, DonationItemStatus status) {
        this.donation = Objects.requireNonNull(donation, "Donation is required");
        this.shrinkageReferenceId = Objects.requireNonNull(shrinkageReferenceId, "Shrinkage reference id is required");
        this.status = Objects.requireNonNull(status, "Status is required");
    }

    public Long getDonationItemId() {
        return getId();
    }

    public Donation getDonation() {
        return donation;
    }

    public ShrinkageReferenceId getShrinkageReferenceId() {
        return shrinkageReferenceId;
    }

    public DonationItemStatus getStatus() {
        return status;
    }

    public void setStatus(DonationItemStatus status) {
        this.status = Objects.requireNonNull(status, "Status is required");
    }
}
