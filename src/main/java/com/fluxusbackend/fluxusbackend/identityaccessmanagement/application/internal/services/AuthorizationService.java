package com.fluxusbackend.fluxusbackend.identityaccessmanagement.application.internal.services;

import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.enums.UserRole;
import com.fluxusbackend.fluxusbackend.shared.application.security.AuthenticatedUserPrincipal;
import com.fluxusbackend.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
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

    public UserRole getCurrentUserRole() {
        var principal = getPrincipal();
        return principal.role();
    }

    public CompanyId getCurrentUserCompanyId() {
        var principal = getPrincipal();
        return principal.companyId();
    }

    public void requireRole(UserRole... allowedRoles) {
        var role = getCurrentUserRole();
        if (Arrays.stream(allowedRoles).noneMatch(r -> r == role)) {
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
