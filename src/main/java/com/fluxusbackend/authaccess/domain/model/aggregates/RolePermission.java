package com.fluxusbackend.authaccess.domain.model.aggregates;

import com.fluxusbackend.shared.domain.model.aggregates.AuditableAggregateRoot;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "role_permission")
@AttributeOverride(name = "id", column = @Column(name = "role_permission_id", nullable = false, updatable = false))
public class RolePermission extends AuditableAggregateRoot {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    protected RolePermission() {
    }

    public RolePermission(Role role, Permission permission) {
        this.role = Objects.requireNonNull(role, "Role is required");
        this.permission = Objects.requireNonNull(permission, "Permission is required");
    }

    public Long getRolePermissionId() {
        return getId();
    }

    public Role getRole() {
        return role;
    }

    public Permission getPermission() {
        return permission;
    }
}
