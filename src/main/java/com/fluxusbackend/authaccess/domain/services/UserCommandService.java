package com.fluxusbackend.authaccess.domain.services;

import com.fluxusbackend.authaccess.domain.model.aggregates.UserAccount;
import com.fluxusbackend.authaccess.domain.model.commands.RegisterUserCommand;

public interface UserCommandService {
    UserAccount handle(RegisterUserCommand command);
}


