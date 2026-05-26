package com.fluxusbackend.subscription.domain.model.aggregates;

import com.fluxusbackend.companyretail.domain.model.aggregates.RetailCompany;
import com.fluxusbackend.subscription.domain.model.enums.SubscriptionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "subscription")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "retail_company_id", nullable = false)
    private RetailCompany retailCompany;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private SubscriptionStatus status;

    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    protected Subscription() {
    }

    public Subscription(RetailCompany retailCompany, Plan plan, SubscriptionStatus status, Instant startDate, Instant endDate) {
        this.retailCompany = Objects.requireNonNull(retailCompany, "Retail company is required");
        this.plan = Objects.requireNonNull(plan, "Plan is required");
        this.status = Objects.requireNonNull(status, "Status is required");
        this.startDate = Objects.requireNonNull(startDate, "Start date is required");
        this.endDate = endDate;
    }

    public Long getSubscriptionId() {
        return id;
    }

    public RetailCompany getRetailCompany() {
        return retailCompany;
    }

    public Plan getPlan() {
        return plan;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }
}
