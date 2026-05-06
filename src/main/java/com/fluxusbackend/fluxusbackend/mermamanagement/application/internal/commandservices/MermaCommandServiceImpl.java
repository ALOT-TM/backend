package com.fluxusbackend.fluxusbackend.mermamanagement.application.internal.commandservices;

import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.aggregates.Merma;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.commands.MarkMermaDonableCommand;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.commands.MarkMermaDonatedCommand;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.commands.MarkMermaNotDonableCommand;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.commands.RegisterMermaCommand;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.services.MermaCommandService;
import com.fluxusbackend.fluxusbackend.mermamanagement.infrastructure.persistence.jpa.repositories.MermaRepository;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class MermaCommandServiceImpl implements MermaCommandService {

    private final MermaRepository repository;
    private final com.fluxusbackend.fluxusbackend.shared.application.security.AclService aclService;

    public MermaCommandServiceImpl(MermaRepository repository,
                                    com.fluxusbackend.fluxusbackend.shared.application.security.AclService aclService) {
        this.repository = repository;
        this.aclService = aclService;
    }

    @Override
    @Transactional
    public Merma handle(RegisterMermaCommand command) {
        var merma = new Merma(
                command.productName(),
                command.categoryName(),
                command.quantity(),
                command.expirationDate(),
                command.reason()
        );
        var companyId = aclService.requireRetailCompanyForCreate();
        merma.setCompanyId(companyId);
        return repository.save(merma);
    }

    @Override
    @Transactional
    public Merma handle(MarkMermaDonableCommand command) {
        var merma = repository.findById(command.mermaId().value())
                .orElseThrow(() -> new NoSuchElementException("Merma not found"));
        aclService.ensureSameCompanyForRetail(merma);
        merma.markDonable();
        return repository.save(merma);
    }

    @Override
    @Transactional
    public Merma handle(MarkMermaNotDonableCommand command) {
        var merma = repository.findById(command.mermaId().value())
                .orElseThrow(() -> new NoSuchElementException("Merma not found"));
        aclService.ensureSameCompanyForRetail(merma);
        merma.markNotDonable();
        return repository.save(merma);
    }

    @Override
    @Transactional
    public Merma handle(MarkMermaDonatedCommand command) {
        var merma = repository.findById(command.mermaId().value())
                .orElseThrow(() -> new NoSuchElementException("Merma not found"));
        aclService.ensureSameCompanyForRetail(merma);
        merma.markDonated();
        return repository.save(merma);
    }
}

