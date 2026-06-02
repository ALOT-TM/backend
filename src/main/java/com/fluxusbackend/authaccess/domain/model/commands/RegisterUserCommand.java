package com.fluxusbackend.authaccess.domain.model.commands;

import com.fluxusbackend.authaccess.domain.model.enums.UserActor;
import com.fluxusbackend.authaccess.domain.model.valueobjects.EmailAddress;
import java.util.Objects;

public record RegisterUserCommand(
        EmailAddress email,
        String rawPassword,
        String username,
        UserActor actor,
        Long retailCompanyId,
        Long beneficiaryInstitutionId
) {
    public RegisterUserCommand {
        if (retailCompanyId != null && retailCompanyId <= 0) {
            retailCompanyId = null;
        }
        if (beneficiaryInstitutionId != null && beneficiaryInstitutionId <= 0) {
            beneficiaryInstitutionId = null;
        }

        Objects.requireNonNull(email, "Email is required");
        Objects.requireNonNull(rawPassword, "Password is required");
        Objects.requireNonNull(username, "Username is required");
        Objects.requireNonNull(actor, "User actor is required");
        if (rawPassword.isBlank() || rawPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (actor == UserActor.RETAIL) {
            if (retailCompanyId == null) {
                throw new IllegalArgumentException("Retail company id is required for RETAIL users");
            }
            if (beneficiaryInstitutionId != null) {
                throw new IllegalArgumentException("Beneficiary institution id must be null for RETAIL users");
            }
        }
        if (actor == UserActor.BENEFICIARY) {
            if (beneficiaryInstitutionId == null) {
                throw new IllegalArgumentException("Beneficiary institution id is required for BENEFICIARY users");
            }
            if (retailCompanyId != null) {
                throw new IllegalArgumentException("Retail company id must be null for BENEFICIARY users");
            }
        }
    }
}
