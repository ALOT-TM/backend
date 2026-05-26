package com.fluxusbackend.shared.application.security;

import com.fluxusbackend.authaccess.domain.model.enums.UserActor;
import com.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
import java.util.Optional;

public interface CurrentUserProvider {

    Optional<CompanyId> getCompanyId();

    Optional<UserActor> getUserActor();
}

