package com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.dto;

import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.aggregates.UserAccount;

public record AuthenticatedUser(UserAccount user, String token) {
}
