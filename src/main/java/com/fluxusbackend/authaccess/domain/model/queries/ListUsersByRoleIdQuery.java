package com.fluxusbackend.authaccess.domain.model.queries;

public record ListUsersByRoleIdQuery(Long roleId) {
    public ListUsersByRoleIdQuery {
        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("Role id must be positive");
        }
    }
}
