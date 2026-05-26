package com.fluxusbackend.donationlogistics.domain.model.aggregates;

import com.fluxusbackend.donationlogistics.domain.model.enums.DonationStatus;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.BeneficiaryReferenceId;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.DeliveryDate;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.DonationId;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.DonationQuantity;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.MermaReferenceId;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.ReceptionDate;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.ScheduledDeliveryDate;
import com.fluxusbackend.shared.domain.model.aggregates.AuditableAggregateRoot;
import com.fluxusbackend.shared.domain.model.aggregates.CompanyScoped;
import com.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
import java.util.Optional;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "donations")
public class Donation extends AuditableAggregateRoot implements CompanyScoped {

    @Embedded
    private MermaReferenceId mermaReferenceId;

    @Embedded
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

    @Embedded
    private CompanyId companyId;

    protected Donation() {
    }

    public Donation(
            MermaReferenceId mermaReferenceId,
            BeneficiaryReferenceId beneficiaryReferenceId,
            DonationQuantity quantity,
            ScheduledDeliveryDate scheduledDeliveryDate
    ) {
        this.mermaReferenceId = Objects.requireNonNull(mermaReferenceId, "Merma reference id is required");
        this.beneficiaryReferenceId = Objects.requireNonNull(beneficiaryReferenceId, "Beneficiary reference id is required");
        this.quantity = Objects.requireNonNull(quantity, "Donation quantity is required");
        this.scheduledDeliveryDate = Objects.requireNonNull(scheduledDeliveryDate, "Scheduled delivery date is required");
        this.status = DonationStatus.ASSIGNED;
    }

    public DonationId getDonationId() {
        return new DonationId(getId());
    }

    public MermaReferenceId getMermaReferenceId() {
        return mermaReferenceId;
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
    }

    public void confirmReception(ReceptionDate receptionDate, Optional<String> comment) {
        if (status != DonationStatus.DELIVERED) {
            throw new IllegalStateException("Donation must be delivered before confirmation");
        }
        this.receptionDate = Objects.requireNonNull(receptionDate, "Reception date is required");
        this.receptionComment = comment == null ? null : comment.orElse(null);
        this.status = DonationStatus.CONFIRMED;
    }
}


