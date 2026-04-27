package com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.services;

import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.aggregates.UserAccount;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.queries.LoginUserQuery;

public interface UserAuthenticationQueryService {
    UserAccount handle(LoginUserQuery query);
}

