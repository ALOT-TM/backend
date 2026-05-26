package com.fluxusbackend.shared.application.security;

import com.fluxusbackend.authaccess.domain.model.enums.UserActor;
import com.fluxusbackend.shared.domain.model.aggregates.CompanyScoped;
import com.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class AclService {

    private final CurrentUserProvider currentUserProvider;

    public AclService(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    public CompanyId requireRetailCompanyForCreate() {
        var actorOpt = currentUserProvider.getUserActor();
        var companyOpt = currentUserProvider.getCompanyId();
        if (actorOpt.isEmpty() || actorOpt.get() != UserActor.RETAIL) {
            throw new SecurityException("Only retail users can perform this operation");
        }
        return companyOpt.orElseThrow(() -> new SecurityException("Current user has no company id"));
    }

    public void ensureSameCompanyForRetail(CompanyScoped entity) {
        var actorOpt = currentUserProvider.getUserActor();
        if (actorOpt.isEmpty() || actorOpt.get() != UserActor.RETAIL) {
            // Non-retail users (beneficiaries) are allowed through for read access to donable items.
            return;
        }
        var companyOpt = currentUserProvider.getCompanyId();
        var entityCompanyOpt = entity.getCompanyId();
        if (companyOpt.isEmpty() || entityCompanyOpt.isEmpty()) {
            throw new SecurityException("Company information missing for ACL check");
        }
        if (!Objects.equals(companyOpt.get().value(), entityCompanyOpt.get().value())) {
            throw new SecurityException("Operation not allowed: entity belongs to a different company");
        }
    }
}

