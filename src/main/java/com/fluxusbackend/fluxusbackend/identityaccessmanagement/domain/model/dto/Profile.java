package com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.dto;

public record Profile(
        Long companyId,
        String email,
        String role
) {
}
