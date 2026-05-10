package com.fluxusbackend.fluxusbackend.donationsmanagement.application.internal.commandservices;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.aggregates.DonationRequest;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.AcceptDonationRequestCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.CancelDonationRequestCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.CreateDonationRequestCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.CreateDonationCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.RejectDonationRequestCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.BeneficiaryReferenceId;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.DonationQuantity;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.MermaReferenceId;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.ScheduledDeliveryDate;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.services.DonationCommandService;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.services.DonationRequestCommandService;
import com.fluxusbackend.fluxusbackend.donationsmanagement.infrastructure.persistence.jpa.repositories.DonationRequestRepository;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.enums.MermaStatus;
import com.fluxusbackend.fluxusbackend.mermamanagement.infrastructure.persistence.jpa.repositories.MermaRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonationRequestCommandServiceImpl implements DonationRequestCommandService {

    private final DonationRequestRepository repository;
    private final MermaRepository mermaRepository;

    public DonationRequestCommandServiceImpl(
            DonationRequestRepository repository,
            MermaRepository mermaRepository
    ) {
        this.repository = repository;
        this.mermaRepository = mermaRepository;
    }

    @Override
    @Transactional
    public DonationRequest handle(CreateDonationRequestCommand command) {
        var mermaRef = new MermaReferenceId(command.mermaId());
        var benefRef = new BeneficiaryReferenceId(command.beneficiaryId());
        var merma = mermaRepository.findById(mermaRef.value())
                .orElseThrow(() -> new IllegalArgumentException("Merma not found"));
        if (merma.getStatus() != MermaStatus.DONABLE) {
            throw new IllegalStateException("Only donable mermas can receive requests");
        }
        var companyId = merma.getCompanyId()
            .orElseThrow(() -> new IllegalArgumentException("Merma does not have an associated company"));        var request = new DonationRequest(mermaRef, benefRef, companyId, command.notes());
        return repository.save(request);
    }

    @Override
    @Transactional
    public DonationRequest handle(AcceptDonationRequestCommand command) {
        var request = repository.findById(command.requestId().value())
            .orElseThrow(() -> new IllegalArgumentException("Donation request not found"));
        request.accept();
        repository.save(request);
        updateMermaToInProcess(request.getMermaReferenceId().value());
        return request;
    }

    @Override
    @Transactional
    public DonationRequest handle(RejectDonationRequestCommand command) {
        var request = repository.findById(command.requestId().value())
            .orElseThrow(() -> new IllegalArgumentException("Donation request not found"));
        request.reject();
        repository.save(request);
        updateMermaToInProcess(request.getMermaReferenceId().value());
        return request;
    }

    @Override
    @Transactional
    public DonationRequest handle(CancelDonationRequestCommand command) {
        var request = repository.findById(command.requestId().value())
            .orElseThrow(() -> new IllegalArgumentException("Donation request not found"));
        request.cancel();
        return repository.save(request);
    }

    private void updateMermaToInProcess(Long mermaId) {
        var merma = mermaRepository.findById(mermaId)
                .orElseThrow(() -> new IllegalArgumentException("Merma not found"));
        if (merma.getStatus() == MermaStatus.DONABLE) {
            merma.markInProcess();
            mermaRepository.save(merma);
        }
    }

}
