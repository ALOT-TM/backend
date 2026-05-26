package com.fluxusbackend.authaccess.domain.model.dto;

import com.fluxusbackend.authaccess.domain.model.dto.UserAccountDto;

public record AuthenticatedUser(UserAccountDto user, String token) {
}

