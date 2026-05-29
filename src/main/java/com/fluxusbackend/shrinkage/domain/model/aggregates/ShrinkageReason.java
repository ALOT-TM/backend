package com.fluxusbackend.shrinkage.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "shrinkage_reason")
public class ShrinkageReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shrinkage_reason_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    protected ShrinkageReason() {
    }

    public ShrinkageReason(String name) {
        this.name = Objects.requireNonNull(name, "Shrinkage reason name is required");
    }

    public Long getShrinkageReasonId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void updateName(String name) {
        this.name = Objects.requireNonNull(name, "Shrinkage reason name is required");
    }
}
