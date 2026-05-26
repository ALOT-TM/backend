package com.fluxusbackend.authaccess.domain.services;

import com.fluxusbackend.authaccess.domain.model.aggregates.UserAccount;
import com.fluxusbackend.authaccess.domain.model.queries.LoginUserQuery;

public interface UserAuthenticationQueryService {
    UserAccount handle(LoginUserQuery query);
}


