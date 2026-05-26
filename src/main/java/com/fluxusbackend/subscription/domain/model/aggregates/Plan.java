package com.fluxusbackend.subscription.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "plan")
public class Plan {

    @Id
    @Column(name = "plan_id", nullable = false, updatable = false)
    private Short id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "max_users", nullable = false)
    private Integer maxUsers;

    @Column(name = "max_storage", nullable = false)
    private Integer maxStorage;

    protected Plan() {
    }

    public Plan(Short id, String name, BigDecimal price, boolean isActive, Integer maxUsers, Integer maxStorage) {
        this.id = Objects.requireNonNull(id, "Plan id is required");
        this.name = Objects.requireNonNull(name, "Plan name is required");
        this.price = Objects.requireNonNull(price, "Plan price is required");
        this.isActive = isActive;
        this.maxUsers = Objects.requireNonNull(maxUsers, "Max users is required");
        this.maxStorage = Objects.requireNonNull(maxStorage, "Max storage is required");
    }

    public Short getPlanId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isActive() {
        return isActive;
    }

    public Integer getMaxUsers() {
        return maxUsers;
    }

    public Integer getMaxStorage() {
        return maxStorage;
    }
}
