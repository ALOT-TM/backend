package com.fluxusbackend.authaccess.application.internal.queryservices;

import com.fluxusbackend.authaccess.application.internal.services.RetailFullAccessRoleService;
import com.fluxusbackend.authaccess.domain.model.aggregates.UserAccount;
import com.fluxusbackend.authaccess.domain.model.queries.LoginUserQuery;
import com.fluxusbackend.authaccess.domain.services.UserAuthenticationQueryService;
import com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories.UserAccountRepository;
import java.util.NoSuchElementException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAuthenticationQueryServiceImpl implements UserAuthenticationQueryService {

    private final UserAccountRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RetailFullAccessRoleService retailFullAccessRoleService;

    public UserAuthenticationQueryServiceImpl(
            UserAccountRepository repository,
            BCryptPasswordEncoder passwordEncoder,
            RetailFullAccessRoleService retailFullAccessRoleService
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.retailFullAccessRoleService = retailFullAccessRoleService;
    }

    @Override
    @Transactional
    public UserAccount handle(LoginUserQuery query) {
        var user = repository.findByEmailValue(query.email().value())
                .orElseThrow(() -> new NoSuchElementException("Invalid credentials"));
        if (!passwordEncoder.matches(query.rawPassword(), user.getPasswordHash().value())) {
            throw new NoSuchElementException("Invalid credentials");
        }
        var retail = user.getRetailUser().orElse(null);
        var beneficiary = user.getBeneficiaryUser().orElse(null);
        if (retail == null && beneficiary == null) {
            throw new NoSuchElementException("User has no profile assigned");
        }
        if (retail != null && !retail.isActive()) {
            throw new NoSuchElementException("Retail user is inactive");
        }
        if (retail != null) {
            var defaultRole = retailFullAccessRoleService.resolveForCompany(retail.getRetailCompany());
            retail.updateRole(defaultRole);
            repository.save(user);
        }
        return user;
    }
}


