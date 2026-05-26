package com.fluxusbackend.shrinkage.application.internal.commandservices;

import com.fluxusbackend.companyretail.infrastructure.persistence.jpa.repositories.RetailCompanyHeadquarterRepository;
import com.fluxusbackend.shrinkage.domain.model.aggregates.Shrinkage;
import com.fluxusbackend.shrinkage.domain.model.commands.MarkShrinkageDonableCommand;
import com.fluxusbackend.shrinkage.domain.model.commands.MarkShrinkageDonatedCommand;
import com.fluxusbackend.shrinkage.domain.model.commands.MarkShrinkageNotDonableCommand;
import com.fluxusbackend.shrinkage.domain.model.commands.RegisterShrinkageCommand;
import com.fluxusbackend.shrinkage.domain.services.ShrinkageCommandService;
import com.fluxusbackend.shrinkage.infrastructure.persistence.jpa.repositories.CategoryRepository;
import com.fluxusbackend.shrinkage.infrastructure.persistence.jpa.repositories.ShrinkageReasonRepository;
import com.fluxusbackend.shrinkage.infrastructure.persistence.jpa.repositories.ShrinkageRepository;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class ShrinkageCommandServiceImpl implements ShrinkageCommandService {

    private final ShrinkageRepository repository;
    private final RetailCompanyHeadquarterRepository headquarterRepository;
    private final CategoryRepository categoryRepository;
    private final ShrinkageReasonRepository shrinkageReasonRepository;
    private final com.fluxusbackend.shared.application.security.AclService aclService;

    public ShrinkageCommandServiceImpl(
            ShrinkageRepository repository,
            RetailCompanyHeadquarterRepository headquarterRepository,
            CategoryRepository categoryRepository,
            ShrinkageReasonRepository shrinkageReasonRepository,
            com.fluxusbackend.shared.application.security.AclService aclService
    ) {
        this.repository = repository;
        this.headquarterRepository = headquarterRepository;
        this.categoryRepository = categoryRepository;
        this.shrinkageReasonRepository = shrinkageReasonRepository;
        this.aclService = aclService;
    }

    @Override
    @Transactional
    public Shrinkage handle(RegisterShrinkageCommand command) {
        var headquarter = headquarterRepository.findById(command.retailCompanyHeadquarterId())
            .orElseThrow(() -> new NoSuchElementException("Retail company headquarter not found"));
        var category = categoryRepository.findById(command.categoryId())
            .orElseThrow(() -> new NoSuchElementException("Category not found"));
        var reason = shrinkageReasonRepository.findById(command.shrinkageReasonId())
            .orElseThrow(() -> new NoSuchElementException("Shrinkage reason not found"));

        var companyId = aclService.requireRetailCompanyForCreate();
        if (!headquarter.getRetailCompany().getRetailCompanyId().equals(companyId.value())) {
            throw new SecurityException("Headquarter does not belong to the current company");
        }

        var shrinkage = new Shrinkage(
            headquarter,
            category,
            reason,
            command.name(),
            command.quantity(),
            command.expirationDate(),
            command.specificReason(),
            command.pickupDate()
        );
        shrinkage.setCompanyId(companyId);
        return repository.save(shrinkage);
    }

    @Override
    @Transactional
    public Shrinkage handle(MarkShrinkageDonableCommand command) {
        var shrinkage = repository.findById(command.shrinkageId().value())
                .orElseThrow(() -> new NoSuchElementException("Shrinkage not found"));
        aclService.ensureSameCompanyForRetail(shrinkage);
        shrinkage.markDonable();
        return repository.save(shrinkage);
    }

    @Override
    @Transactional
    public Shrinkage handle(MarkShrinkageNotDonableCommand command) {
        var shrinkage = repository.findById(command.shrinkageId().value())
                .orElseThrow(() -> new NoSuchElementException("Shrinkage not found"));
        aclService.ensureSameCompanyForRetail(shrinkage);
        shrinkage.markNotDonable();
        return repository.save(shrinkage);
    }

    @Override
    @Transactional
    public Shrinkage handle(MarkShrinkageDonatedCommand command) {
        var shrinkage = repository.findById(command.shrinkageId().value())
                .orElseThrow(() -> new NoSuchElementException("Shrinkage not found"));
        aclService.ensureSameCompanyForRetail(shrinkage);
        shrinkage.markDonated();
        return repository.save(shrinkage);
    }
}


