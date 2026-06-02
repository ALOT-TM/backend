package com.fluxusbackend.authaccess.domain.services;

import com.fluxusbackend.authaccess.domain.model.aggregates.UserAccount;
import com.fluxusbackend.authaccess.domain.model.commands.RegisterUserCommand;
import com.fluxusbackend.authaccess.domain.model.commands.UpdateProfileCommand;
import com.fluxusbackend.authaccess.domain.model.commands.ChangePasswordCommand;

public interface UserCommandService {
    UserAccount handle(RegisterUserCommand command);
    UserAccount handle(UpdateProfileCommand command);
    void handle(ChangePasswordCommand command);
}


