package com.fluxusbackend.fluxusbackend.identityaccessmanagement.application.internal.queryservices;

import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.aggregates.UserAccount;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.queries.LoginUserQuery;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.services.UserAuthenticationQueryService;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.infrastructure.persistence.jpa.repositories.UserAccountRepository;
import java.util.NoSuchElementException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAuthenticationQueryServiceImpl implements UserAuthenticationQueryService {

    private final UserAccountRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserAuthenticationQueryServiceImpl(UserAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccount handle(LoginUserQuery query) {
        var user = repository.findByEmailValue(query.email().value())
                .orElseThrow(() -> new NoSuchElementException("Invalid credentials"));
        if (!passwordEncoder.matches(query.rawPassword(), user.getPasswordHash().value())) {
            throw new NoSuchElementException("Invalid credentials");
        }
        if (user.getRole() == com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.enums.UserRole.MANAGER && user.getCompanyId().isEmpty()) {
            throw new NoSuchElementException("User has no company assigned");
        }
        return user;
    }
}

