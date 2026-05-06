package com.fluxusbackend.fluxusbackend.shared.application.security;

import com.fluxusbackend.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.enums.UserRole;
import java.util.Optional;

public interface CurrentUserProvider {

    Optional<CompanyId> getCompanyId();

    Optional<UserRole> getUserRole();
}
