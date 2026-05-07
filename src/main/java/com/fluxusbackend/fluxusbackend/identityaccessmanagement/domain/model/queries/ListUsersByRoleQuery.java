package com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.queries;

import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.enums.UserRole;

public record ListUsersByRoleQuery(UserRole role) {
    public ListUsersByRoleQuery {
    }
}
