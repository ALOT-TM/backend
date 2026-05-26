package com.fluxusbackend.authaccess.application.internal.commandservices;

import com.fluxusbackend.authaccess.domain.model.aggregates.BeneficiaryUser;
import com.fluxusbackend.authaccess.domain.model.aggregates.RetailUser;
import com.fluxusbackend.authaccess.domain.model.aggregates.UserAccount;
import com.fluxusbackend.authaccess.domain.model.commands.RegisterUserCommand;
import com.fluxusbackend.authaccess.domain.model.enums.UserActor;
import com.fluxusbackend.authaccess.domain.model.valueobjects.PasswordHash;
import com.fluxusbackend.authaccess.domain.services.UserCommandService;
import com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories.RoleRepository;
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
    private final RoleRepository roleRepository;
    private final BeneficiaryInstitutionRepository beneficiaryInstitutionRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserCommandServiceImpl(
            UserAccountRepository repository,
            RetailCompanyRepository retailCompanyRepository,
            RoleRepository roleRepository,
            BeneficiaryInstitutionRepository beneficiaryInstitutionRepository
    ) {
        this.repository = repository;
        this.retailCompanyRepository = retailCompanyRepository;
        this.roleRepository = roleRepository;
        this.beneficiaryInstitutionRepository = beneficiaryInstitutionRepository;
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
            var role = roleRepository.findById(command.roleId())
                    .orElseThrow(() -> new NoSuchElementException("Role not found"));
            if (!role.getRetailCompany().getRetailCompanyId().equals(company.getRetailCompanyId())) {
                throw new IllegalArgumentException("Role does not belong to the retail company");
            }
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
}


