package com.fluxusbackend.shared.application.audit;

import com.fluxusbackend.shared.application.security.AuthenticatedUserPrincipal;
import com.fluxusbackend.shared.application.security.JwtTokenService;
import com.fluxusbackend.shared.domain.model.aggregates.StatusChangeLog;
import com.fluxusbackend.shared.infrastructure.persistence.jpa.repositories.StatusChangeLogRepository;
import java.time.Instant;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class StatusChangeLogService {

    private final StatusChangeLogRepository repository;
    private final JwtTokenService jwtTokenService;

    public StatusChangeLogService(StatusChangeLogRepository repository, JwtTokenService jwtTokenService) {
        this.repository = repository;
        this.jwtTokenService = jwtTokenService;
    }

    public void recordChange(String entityType, Long entityId, String fromStatus, String toStatus) {
        var log = new StatusChangeLog(
                entityType,
                entityId,
                fromStatus,
                toStatus,
                resolveUserId().orElse(null),
                Instant.now()
        );
        repository.save(log);
    }

    private Optional<Long> resolveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUserPrincipal principal) {
            return Optional.ofNullable(principal.userId());
        }
        var header = getRequestAuthorizationHeader().orElse(null);
        var token = jwtTokenService.getBearerTokenFrom(header);
        return jwtTokenService.parseToken(token).map(JwtTokenService.AuthInfo::userId);
    }

    private Optional<String> getRequestAuthorizationHeader() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletAttrs) {
            return Optional.ofNullable(servletAttrs.getRequest().getHeader("Authorization"));
        }
        return Optional.empty();
    }
}