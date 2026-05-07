package com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.services;

import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.aggregates.UserAccount;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.queries.GetUserByEmailQuery;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.queries.GetUserByIdQuery;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.queries.ListUsersByRoleQuery;
import java.util.Optional;

public interface UserQueryService {
    Optional<UserAccount> handle(GetUserByIdQuery query);

    Optional<UserAccount> handle(GetUserByEmailQuery query);

    java.util.List<UserAccount> handle(ListUsersByRoleQuery query);
    java.util.List<UserAccount> findAll();
}

