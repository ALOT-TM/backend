package com.fluxusbackend.authaccess.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "permission")
public class Permission {

    @Id
    @Column(name = "permission_id", nullable = false, updatable = false)
    private Short id;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    protected Permission() {
    }

    public Permission(Short id, String description) {
        this.id = Objects.requireNonNull(id, "Permission id is required");
        this.description = Objects.requireNonNull(description, "Description is required");
    }

    public Short getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
