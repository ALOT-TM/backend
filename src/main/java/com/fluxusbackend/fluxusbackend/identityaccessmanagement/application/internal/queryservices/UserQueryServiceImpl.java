package com.fluxusbackend.fluxusbackend.identityaccessmanagement.application.internal.queryservices;

import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.aggregates.UserAccount;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.queries.GetUserByEmailQuery;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.queries.GetUserByIdQuery;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.services.UserQueryService;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.infrastructure.persistence.jpa.repositories.UserAccountRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final UserAccountRepository repository;

    public UserQueryServiceImpl(UserAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAccount> handle(GetUserByIdQuery query) {
        return repository.findById(query.userId().value());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAccount> handle(GetUserByEmailQuery query) {
        return repository.findByEmailValue(query.email().value());
    }
}

