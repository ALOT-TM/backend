package com.fluxusbackend.fluxusbackend.donationsmanagement.application.internal.commandservices;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.aggregates.DonationRequest;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.AcceptDonationRequestCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.CancelDonationRequestCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.CreateDonationRequestCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.RejectDonationRequestCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.BeneficiaryReferenceId;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.MermaReferenceId;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.services.DonationRequestCommandService;
import com.fluxusbackend.fluxusbackend.donationsmanagement.infrastructure.persistence.jpa.repositories.DonationRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonationRequestCommandServiceImpl implements DonationRequestCommandService {

    private final DonationRequestRepository repository;

    public DonationRequestCommandServiceImpl(DonationRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public DonationRequest handle(CreateDonationRequestCommand command) {
        var mermaRef = new MermaReferenceId(command.mermaId());
        var benefRef = new BeneficiaryReferenceId(command.beneficiaryId());
        var request = new DonationRequest(mermaRef, benefRef, command.notes());
        return repository.save(request);
    }

    @Override
    @Transactional
    public DonationRequest handle(AcceptDonationRequestCommand command) {
        var request = repository.findById(command.requestId().value())
            .orElseThrow(() -> new IllegalArgumentException("Donation request not found"));
        request.accept();
        return repository.save(request);
    }

    @Override
    @Transactional
    public DonationRequest handle(RejectDonationRequestCommand command) {
        var request = repository.findById(command.requestId().value())
            .orElseThrow(() -> new IllegalArgumentException("Donation request not found"));
        request.reject();
        return repository.save(request);
    }

    @Override
    @Transactional
    public DonationRequest handle(CancelDonationRequestCommand command) {
        var request = repository.findById(command.requestId().value())
            .orElseThrow(() -> new IllegalArgumentException("Donation request not found"));
        request.cancel();
        return repository.save(request);
    }
}
