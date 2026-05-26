package com.fluxusbackend.authaccess.domain.model.dto;

public record Profile(
        Long retailCompanyId,
        Long beneficiaryInstitutionId,
        String email,
        String actor,
        String roleName
) {
}

