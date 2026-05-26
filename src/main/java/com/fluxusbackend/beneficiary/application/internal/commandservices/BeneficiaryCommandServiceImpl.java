package com.fluxusbackend.beneficiary.application.internal.commandservices;

import com.fluxusbackend.beneficiary.domain.model.aggregates.BeneficiaryInstitution;
import com.fluxusbackend.beneficiary.domain.model.commands.RegisterBeneficiaryCommand;
import com.fluxusbackend.beneficiary.domain.model.commands.UpdateBeneficiaryInfoCommand;
import com.fluxusbackend.beneficiary.domain.services.BeneficiaryCommandService;
import com.fluxusbackend.beneficiary.infrastructure.persistence.jpa.repositories.BeneficiaryInstitutionRepository;
import com.fluxusbackend.beneficiary.infrastructure.persistence.jpa.repositories.InstitutionTypeRepository;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class BeneficiaryCommandServiceImpl implements BeneficiaryCommandService {

    private final BeneficiaryInstitutionRepository repository;
    private final InstitutionTypeRepository institutionTypeRepository;

    public BeneficiaryCommandServiceImpl(
            BeneficiaryInstitutionRepository repository,
            InstitutionTypeRepository institutionTypeRepository
    ) {
        this.repository = repository;
        this.institutionTypeRepository = institutionTypeRepository;
    }

    @Override
    @Transactional
    public BeneficiaryInstitution handle(RegisterBeneficiaryCommand command) {
        var type = institutionTypeRepository.findById(command.institutionTypeId())
                .orElseThrow(() -> new NoSuchElementException("Institution type not found"));
        var beneficiary = new BeneficiaryInstitution(type, command.name());
        return repository.save(beneficiary);
    }

    @Override
    @Transactional
    public BeneficiaryInstitution handle(UpdateBeneficiaryInfoCommand command) {
        var beneficiary = repository.findById(command.beneficiaryId().value())
                .orElseThrow(() -> new NoSuchElementException("Beneficiary institution not found"));
        var type = institutionTypeRepository.findById(command.institutionTypeId())
                .orElseThrow(() -> new NoSuchElementException("Institution type not found"));
        beneficiary.updateInfo(type, command.name());
        return repository.save(beneficiary);
    }
}


