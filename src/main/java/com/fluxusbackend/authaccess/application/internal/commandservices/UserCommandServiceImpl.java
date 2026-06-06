package com.fluxusbackend.authaccess.application.internal.commandservices;

import com.fluxusbackend.authaccess.domain.model.aggregates.BeneficiaryUser;
import com.fluxusbackend.authaccess.domain.model.aggregates.RetailUser;
import com.fluxusbackend.authaccess.domain.model.aggregates.UserAccount;
import com.fluxusbackend.authaccess.application.internal.services.RetailFullAccessRoleService;
import com.fluxusbackend.authaccess.domain.model.commands.RegisterUserCommand;
import com.fluxusbackend.authaccess.domain.model.commands.UpdateProfileCommand;
import com.fluxusbackend.authaccess.domain.model.commands.ChangePasswordCommand;
import com.fluxusbackend.authaccess.domain.model.enums.UserActor;
import com.fluxusbackend.authaccess.domain.model.valueobjects.PasswordHash;
import com.fluxusbackend.authaccess.domain.services.UserCommandService;
import com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories.UserAccountRepository;
import com.fluxusbackend.beneficiary.infrastructure.persistence.jpa.repositories.BeneficiaryInstitutionRepository;
import com.fluxusbackend.companyretail.infrastructure.persistence.jpa.repositories.RetailCompanyRepository;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserAccountRepository repository;
    private final RetailCompanyRepository retailCompanyRepository;
    private final BeneficiaryInstitutionRepository beneficiaryInstitutionRepository;
    private final RetailFullAccessRoleService retailFullAccessRoleService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserCommandServiceImpl(
            UserAccountRepository repository,
            RetailCompanyRepository retailCompanyRepository,
            BeneficiaryInstitutionRepository beneficiaryInstitutionRepository,
            RetailFullAccessRoleService retailFullAccessRoleService,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.retailCompanyRepository = retailCompanyRepository;
        this.beneficiaryInstitutionRepository = beneficiaryInstitutionRepository;
        this.retailFullAccessRoleService = retailFullAccessRoleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserAccount handle(RegisterUserCommand command) {
        var existing = repository.findByEmailValue(command.email().value());
        if (existing.isPresent()) {
            throw new NoSuchElementException("Email already registered");
        }
        var hash = new PasswordHash(passwordEncoder.encode(command.rawPassword()));
        var user = new UserAccount(command.email(), hash, command.username());

        if (command.actor() == UserActor.RETAIL) {
            var company = retailCompanyRepository.findById(command.retailCompanyId())
                    .orElseThrow(() -> new NoSuchElementException("Retail company not found"));
            var role = retailFullAccessRoleService.resolveForCompany(company);
            var retailUser = new RetailUser(user, company, role, true);
            user.attachRetailUser(retailUser);
        } else {
            var institution = beneficiaryInstitutionRepository.findById(command.beneficiaryInstitutionId())
                    .orElseThrow(() -> new NoSuchElementException("Beneficiary institution not found"));
            var beneficiaryUser = new BeneficiaryUser(user, institution);
            user.attachBeneficiaryUser(beneficiaryUser);
        }

        return repository.save(user);
    }

    @Override
    @Transactional
    public UserAccount handle(UpdateProfileCommand command) {
        var user = repository.findById(command.userId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        var existing = repository.findByEmailValue(command.email());
        if (existing.isPresent() && !existing.get().getUserId().value().equals(command.userId())) {
            throw new IllegalArgumentException("Email already in use");
        }
        user.updateProfile(command.username(), new com.fluxusbackend.authaccess.domain.model.valueobjects.EmailAddress(command.email()));
        return repository.save(user);
    }

    @Override
    @Transactional
    public void handle(ChangePasswordCommand command) {
        var user = repository.findById(command.userId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        if (!passwordEncoder.matches(command.currentPassword(), user.getPasswordHash().value())) {
            throw new IllegalArgumentException("Current password does not match");
        }
        var newHash = new PasswordHash(passwordEncoder.encode(command.newPassword()));
        user.updatePassword(newHash);
        repository.save(user);
    }
}
