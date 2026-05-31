package com.fluxusbackend.shrinkage.application.internal.commandservices;

import com.fluxusbackend.companyretail.infrastructure.persistence.jpa.repositories.RetailCompanyHeadquarterRepository;
import com.fluxusbackend.shrinkage.domain.model.aggregates.Shrinkage;
import com.fluxusbackend.shrinkage.domain.model.commands.MarkShrinkageDonableCommand;
import com.fluxusbackend.shrinkage.domain.model.commands.MarkShrinkageDonatedCommand;
import com.fluxusbackend.shrinkage.domain.model.commands.MarkShrinkageNotDonableCommand;
import com.fluxusbackend.shrinkage.domain.model.commands.RegisterShrinkageCommand;
import com.fluxusbackend.shrinkage.domain.model.events.ShrinkageStatusChangedEvent;
import com.fluxusbackend.shrinkage.domain.model.valueobjects.ShrinkageId;
import com.fluxusbackend.shrinkage.domain.services.ShrinkageCommandService;
import com.fluxusbackend.shrinkage.infrastructure.persistence.jpa.repositories.CategoryRepository;
import com.fluxusbackend.shrinkage.infrastructure.persistence.jpa.repositories.ShrinkageReasonRepository;
import com.fluxusbackend.shrinkage.infrastructure.persistence.jpa.repositories.ShrinkageRepository;
import com.fluxusbackend.shared.application.audit.StatusChangeLogService;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.NoSuchElementException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class ShrinkageCommandServiceImpl implements ShrinkageCommandService {

    private final ShrinkageRepository repository;
    private final RetailCompanyHeadquarterRepository headquarterRepository;
    private final CategoryRepository categoryRepository;
    private final ShrinkageReasonRepository shrinkageReasonRepository;
    private final com.fluxusbackend.shared.application.security.AclService aclService;
    private final StatusChangeLogService statusChangeLogService;

    public ShrinkageCommandServiceImpl(
            ShrinkageRepository repository,
            RetailCompanyHeadquarterRepository headquarterRepository,
            CategoryRepository categoryRepository,
            ShrinkageReasonRepository shrinkageReasonRepository,
            com.fluxusbackend.shared.application.security.AclService aclService,
            StatusChangeLogService statusChangeLogService
    ) {
        this.repository = repository;
        this.headquarterRepository = headquarterRepository;
        this.categoryRepository = categoryRepository;
        this.shrinkageReasonRepository = shrinkageReasonRepository;
        this.aclService = aclService;
        this.statusChangeLogService = statusChangeLogService;
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
        var saved = repository.save(shrinkage);
        return saved;
    }

    @Override
    @Transactional
    public Shrinkage handle(MarkShrinkageDonableCommand command) {
        var shrinkage = repository.findById(command.shrinkageId().value())
                .orElseThrow(() -> new NoSuchElementException("Shrinkage not found"));
        aclService.ensureSameCompanyForRetail(shrinkage);
        var fromStatus = shrinkage.getStatus();
        shrinkage.markDonable();
        var saved = repository.save(shrinkage);
        statusChangeLogService.recordChange(
            "SHRINKAGE",
            saved.getShrinkageId(),
            fromStatus.name(),
            saved.getStatus().name()
        );
        return saved;
    }

    @Override
    @Transactional
    public Shrinkage handle(MarkShrinkageNotDonableCommand command) {
        var shrinkage = repository.findById(command.shrinkageId().value())
                .orElseThrow(() -> new NoSuchElementException("Shrinkage not found"));
        aclService.ensureSameCompanyForRetail(shrinkage);
        var fromStatus = shrinkage.getStatus();
        shrinkage.markNotDonable();
        var saved = repository.save(shrinkage);
        statusChangeLogService.recordChange(
            "SHRINKAGE",
            saved.getShrinkageId(),
            fromStatus.name(),
            saved.getStatus().name()
        );
        return saved;
    }

    @Override
    @Transactional
    public Shrinkage handle(MarkShrinkageDonatedCommand command) {
        var shrinkage = repository.findById(command.shrinkageId().value())
                .orElseThrow(() -> new NoSuchElementException("Shrinkage not found"));
        aclService.ensureSameCompanyForRetail(shrinkage);
        var fromStatus = shrinkage.getStatus();
        shrinkage.markDonated();
        var saved = repository.save(shrinkage);
        statusChangeLogService.recordChange(
            "SHRINKAGE",
            saved.getShrinkageId(),
            fromStatus.name(),
            saved.getStatus().name()
        );
        return saved;
    }
}


