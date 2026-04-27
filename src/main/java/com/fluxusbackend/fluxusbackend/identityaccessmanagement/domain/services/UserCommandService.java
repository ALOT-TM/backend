package com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.services;

import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.aggregates.UserAccount;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.commands.RegisterUserCommand;

public interface UserCommandService {
    UserAccount handle(RegisterUserCommand command);
}

