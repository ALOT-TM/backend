package com.fluxusbackend.fluxusbackend.identityaccessmanagement.application.internal.services;

import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.enums.UserRole;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.queries.GetUserByIdQuery;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.valueobjects.UserId;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.services.UserQueryService;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthorizationService {

    private final UserQueryService userQueryService;

    public AuthorizationService(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    public void requireRole(Long userId, UserRole... allowedRoles) {
        var user = userQueryService.handle(new GetUserByIdQuery(new UserId(userId)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));
        if (Arrays.stream(allowedRoles).noneMatch(r -> r == user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }
}
