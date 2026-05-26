package com.fluxusbackend.authaccess.application.internal.queryservices;

import com.fluxusbackend.authaccess.domain.model.aggregates.UserAccount;
import com.fluxusbackend.authaccess.domain.model.queries.GetUserByEmailQuery;
import com.fluxusbackend.authaccess.domain.model.queries.GetUserByIdQuery;
import com.fluxusbackend.authaccess.domain.model.queries.ListUsersByRoleIdQuery;
import com.fluxusbackend.authaccess.domain.services.UserQueryService;
import com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories.UserAccountRepository;
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

    @Override
    @Transactional(readOnly = true)
    public java.util.List<UserAccount> handle(ListUsersByRoleIdQuery query) {
        return repository.findAllRetailUsersByRoleId(query.roleId());
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<UserAccount> findAll() {
        return repository.findAll();
    }
}


