package com.fluxusbackend.donationlogistics.domain.model.aggregates;

import com.fluxusbackend.donationlogistics.domain.model.valueobjects.ShrinkageReferenceId;
import com.fluxusbackend.shrinkage.domain.model.enums.ShrinkageStatus;
import com.fluxusbackend.shared.domain.model.aggregates.AuditableAggregateRoot;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "shrinkage_log")
@AttributeOverride(name = "id", column = @Column(name = "shrinkage_log_id", nullable = false, updatable = false))
public class ShrinkageLog extends AuditableAggregateRoot {

    @Embedded
    private ShrinkageReferenceId shrinkageReferenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 30)
    private ShrinkageStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 30)
    private ShrinkageStatus newStatus;

    @Column(name = "logged_at", nullable = false)
    private Instant loggedAt;

    protected ShrinkageLog() {
    }

    public ShrinkageLog(ShrinkageReferenceId shrinkageReferenceId, ShrinkageStatus oldStatus, ShrinkageStatus newStatus) {
        this.shrinkageReferenceId = Objects.requireNonNull(shrinkageReferenceId, "Shrinkage reference id is required");
        this.oldStatus = oldStatus;
        this.newStatus = Objects.requireNonNull(newStatus, "New status is required");
        this.loggedAt = Instant.now();
    }

    public Long getShrinkageLogId() {
        return getId();
    }

    public ShrinkageReferenceId getShrinkageReferenceId() {
        return shrinkageReferenceId;
    }

    public ShrinkageStatus getOldStatus() {
        return oldStatus;
    }

    public ShrinkageStatus getNewStatus() {
        return newStatus;
    }

    public Instant getLoggedAt() {
        return loggedAt;
    }
}
