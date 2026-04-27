package com.fluxusbackend.fluxusbackend.identityaccessmanagement.application.internal.commandservices;

import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.aggregates.UserAccount;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.commands.RegisterUserCommand;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.valueobjects.PasswordHash;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.services.UserCommandService;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.infrastructure.persistence.jpa.repositories.UserAccountRepository;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserAccountRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserCommandServiceImpl(UserAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public UserAccount handle(RegisterUserCommand command) {
        var existing = repository.findByEmailValue(command.email().value());
        if (existing.isPresent()) {
            throw new NoSuchElementException("Email already registered");
        }
        var hash = new PasswordHash(passwordEncoder.encode(command.rawPassword()));
        var user = new UserAccount(command.email(), hash, command.role());
        return repository.save(user);
    }
}

