package com.fluxusbackend.donationlogistics.domain.model.aggregates;

import com.fluxusbackend.donationlogistics.domain.model.enums.DonationRequestStatus;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.BeneficiaryReferenceId;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.ShrinkageReferenceId;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.DonationRequestId;
import com.fluxusbackend.shared.domain.model.aggregates.AuditableAggregateRoot;
import com.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "donation_requests")
public class DonationRequest extends AuditableAggregateRoot {

    @Embedded
    private ShrinkageReferenceId shrinkageReferenceId;

    @Embedded
    private BeneficiaryReferenceId beneficiaryReferenceId;

    @Embedded
    private CompanyId companyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private DonationRequestStatus status;

    @Column(name = "notes", length = 500)
    private String notes;

    protected DonationRequest() {
    }

    public DonationRequest(
            ShrinkageReferenceId shrinkageReferenceId,
            BeneficiaryReferenceId beneficiaryReferenceId,
            CompanyId companyId,
            String notes
    ) {
        this.shrinkageReferenceId = Objects.requireNonNull(shrinkageReferenceId, "Shrinkage reference is required");
        this.beneficiaryReferenceId = Objects.requireNonNull(beneficiaryReferenceId, "Beneficiary reference is required");
        this.companyId = Objects.requireNonNull(companyId, "Company ID is required");
        this.notes = notes;
        this.status = DonationRequestStatus.PENDING;
    }

    public DonationRequestId getDonationRequestId() {
        return new DonationRequestId(getId());
    }

    public ShrinkageReferenceId getShrinkageReferenceId() {
        return shrinkageReferenceId;
    }

    public BeneficiaryReferenceId getBeneficiaryReferenceId() {
        return beneficiaryReferenceId;
    }

    public CompanyId getCompanyId() {
        return companyId;
    }

    public DonationRequestStatus getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public void accept() {
        if (status != DonationRequestStatus.PENDING) {
            throw new IllegalStateException("Only pending donation requests can be accepted");
        }
        status = DonationRequestStatus.ACCEPTED;
    }

    public void reject() {
        if (status != DonationRequestStatus.PENDING) {
            throw new IllegalStateException("Only pending donation requests can be rejected");
        }
        status = DonationRequestStatus.REJECTED;
    }

    public void cancel() {
        if (status == DonationRequestStatus.COMPLETED || status == DonationRequestStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel completed or already cancelled donation requests");
        }
        status = DonationRequestStatus.CANCELLED;
    }

    public void complete() {
        if (status != DonationRequestStatus.ACCEPTED) {
            throw new IllegalStateException("Only accepted donation requests can be completed");
        }
        status = DonationRequestStatus.COMPLETED;
    }
}

