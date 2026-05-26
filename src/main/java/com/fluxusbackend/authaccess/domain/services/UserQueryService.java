package com.fluxusbackend.authaccess.domain.services;

import com.fluxusbackend.authaccess.domain.model.aggregates.UserAccount;
import com.fluxusbackend.authaccess.domain.model.queries.GetUserByEmailQuery;
import com.fluxusbackend.authaccess.domain.model.queries.GetUserByIdQuery;
import com.fluxusbackend.authaccess.domain.model.queries.ListUsersByRoleIdQuery;
import java.util.Optional;

public interface UserQueryService {
    Optional<UserAccount> handle(GetUserByIdQuery query);

    Optional<UserAccount> handle(GetUserByEmailQuery query);

    java.util.List<UserAccount> handle(ListUsersByRoleIdQuery query);
    java.util.List<UserAccount> findAll();
}


