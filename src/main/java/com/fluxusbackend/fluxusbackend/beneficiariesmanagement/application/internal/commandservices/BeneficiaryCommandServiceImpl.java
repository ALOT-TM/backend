package com.fluxusbackend.fluxusbackend.beneficiariesmanagement.application.internal.commandservices;

import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.aggregates.Beneficiary;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.commands.ActivateBeneficiaryCommand;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.commands.DeactivateBeneficiaryCommand;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.commands.RegisterBeneficiaryCommand;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.commands.UpdateBeneficiaryInfoCommand;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.services.BeneficiaryCommandService;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.infrastructure.persistence.jpa.repositories.BeneficiaryRepository;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class BeneficiaryCommandServiceImpl implements BeneficiaryCommandService {

    private final BeneficiaryRepository repository;

    public BeneficiaryCommandServiceImpl(BeneficiaryRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Beneficiary handle(RegisterBeneficiaryCommand command) {
        var beneficiary = new Beneficiary(
                command.name(),
                command.type(),
                command.address(),
                command.acceptedProducts()
        );
        return repository.save(beneficiary);
    }

    @Override
    @Transactional
    public Beneficiary handle(UpdateBeneficiaryInfoCommand command) {
        var beneficiary = repository.findById(command.beneficiaryId().value())
                .orElseThrow(() -> new NoSuchElementException("Beneficiary not found"));
        beneficiary.updateInfo(command.name(), command.type(), command.address(), command.acceptedProducts());
        return repository.save(beneficiary);
    }

    @Override
    @Transactional
    public Beneficiary handle(ActivateBeneficiaryCommand command) {
        var beneficiary = repository.findById(command.beneficiaryId().value())
                .orElseThrow(() -> new NoSuchElementException("Beneficiary not found"));
        beneficiary.activate();
        return repository.save(beneficiary);
    }

    @Override
    @Transactional
    public Beneficiary handle(DeactivateBeneficiaryCommand command) {
        var beneficiary = repository.findById(command.beneficiaryId().value())
                .orElseThrow(() -> new NoSuchElementException("Beneficiary not found"));
        beneficiary.deactivate();
        return repository.save(beneficiary);
    }
}

