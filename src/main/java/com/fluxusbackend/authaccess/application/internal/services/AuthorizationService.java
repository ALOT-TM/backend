package com.fluxusbackend.authaccess.application.internal.services;

import com.fluxusbackend.authaccess.domain.model.enums.UserActor;
import com.fluxusbackend.shared.application.security.AuthenticatedUserPrincipal;
import com.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthorizationService {

    public Long getCurrentUserId() {
        var principal = getPrincipal();
        return principal.userId();
    }

    public UserActor getCurrentUserActor() {
        var principal = getPrincipal();
        return principal.actor();
    }

    public CompanyId getCurrentUserCompanyId() {
        var principal = getPrincipal();
        return principal.companyId();
    }

    public Long getCurrentBeneficiaryInstitutionId() {
        var principal = getPrincipal();
        return principal.beneficiaryInstitutionId();
    }

    public Long getCurrentRoleId() {
        var principal = getPrincipal();
        return principal.roleId();
    }

    public void requireActor(UserActor... allowedActors) {
        var actor = getCurrentUserActor();
        if (Arrays.stream(allowedActors).noneMatch(r -> r == actor)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    private AuthenticatedUserPrincipal getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthenticatedUserPrincipal principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return principal;
    }
}

