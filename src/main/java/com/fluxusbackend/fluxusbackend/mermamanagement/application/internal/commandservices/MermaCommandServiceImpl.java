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

    public MermaCommandServiceImpl(MermaRepository repository) {
        this.repository = repository;
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
        return repository.save(merma);
    }

    @Override
    @Transactional
    public Merma handle(MarkMermaDonableCommand command) {
        var merma = repository.findById(command.mermaId().value())
                .orElseThrow(() -> new NoSuchElementException("Merma not found"));
        merma.markDonable();
        return repository.save(merma);
    }

    @Override
    @Transactional
    public Merma handle(MarkMermaNotDonableCommand command) {
        var merma = repository.findById(command.mermaId().value())
                .orElseThrow(() -> new NoSuchElementException("Merma not found"));
        merma.markNotDonable();
        return repository.save(merma);
    }

    @Override
    @Transactional
    public Merma handle(MarkMermaDonatedCommand command) {
        var merma = repository.findById(command.mermaId().value())
                .orElseThrow(() -> new NoSuchElementException("Merma not found"));
        merma.markDonated();
        return repository.save(merma);
    }
}

