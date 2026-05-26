package com.fluxusbackend.shared.application.security;

import com.fluxusbackend.authaccess.domain.model.enums.UserActor;
import com.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class HeaderCurrentUserProvider implements CurrentUserProvider {

    private final JwtTokenService jwtTokenService;

    public HeaderCurrentUserProvider(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Optional<CompanyId> getCompanyId() {
        return getPrincipalCompanyId().or(this::getCompanyIdFromJwt).or(this::getCompanyIdFromLegacyHeader);
    }

    @Override
    public Optional<UserActor> getUserActor() {
        return getPrincipalActor().or(this::getUserActorFromJwt).or(this::getUserActorFromLegacyHeader);
    }

    private Optional<CompanyId> getPrincipalCompanyId() {
        return getPrincipal().map(AuthenticatedUserPrincipal::companyId);
    }

    private Optional<UserActor> getPrincipalActor() {
        return getPrincipal().map(AuthenticatedUserPrincipal::actor);
    }

    private Optional<AuthenticatedUserPrincipal> getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUserPrincipal principal)) {
            return Optional.empty();
        }
        return Optional.of(principal);
    }

    private Optional<CompanyId> getCompanyIdFromJwt() {
        return getRequest()
                .map(req -> req.getHeader("Authorization"))
                .flatMap(jwtTokenService::parseToken)
                .map(JwtTokenService.AuthInfo::companyId);
    }

    private Optional<UserActor> getUserActorFromJwt() {
        return getRequest()
                .map(req -> req.getHeader("Authorization"))
                .flatMap(jwtTokenService::parseToken)
                .map(JwtTokenService.AuthInfo::actor);
    }

    private Optional<CompanyId> getCompanyIdFromLegacyHeader() {
        return getRequest()
                .map(req -> req.getHeader("X-User-Company-Id"))
                .flatMap(this::parseCompanyId);
    }

    private Optional<UserActor> getUserActorFromLegacyHeader() {
        return getRequest()
                .map(req -> req.getHeader("X-User-Actor"))
                .flatMap(this::parseUserActor);
    }

    private Optional<HttpServletRequest> getRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletAttrs) {
            return Optional.ofNullable(servletAttrs.getRequest());
        }
        return Optional.empty();
    }

    private Optional<CompanyId> parseCompanyId(String header) {
        if (header == null || header.isBlank()) return Optional.empty();
        try {
            var id = Long.parseLong(header.trim());
            return Optional.of(new CompanyId(id));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private Optional<UserActor> parseUserActor(String header) {
        if (header == null || header.isBlank()) return Optional.empty();
        try {
            return Optional.of(UserActor.valueOf(header.trim()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}

