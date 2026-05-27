package com.fluxusbackend.donationlogistics.application.internal.commandservices;

import com.fluxusbackend.donationlogistics.domain.model.aggregates.DonationRequest;
import com.fluxusbackend.donationlogistics.domain.model.commands.AcceptDonationRequestCommand;
import com.fluxusbackend.donationlogistics.domain.model.commands.CancelDonationRequestCommand;
import com.fluxusbackend.donationlogistics.domain.model.commands.CreateDonationRequestCommand;

import com.fluxusbackend.donationlogistics.domain.model.commands.RejectDonationRequestCommand;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.BeneficiaryReferenceId;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.DonationQuantity;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.ShrinkageReferenceId;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.ScheduledDeliveryDate;
import com.fluxusbackend.donationlogistics.domain.services.DonationCommandService;
import com.fluxusbackend.donationlogistics.domain.services.DonationRequestCommandService;
import com.fluxusbackend.donationlogistics.infrastructure.persistence.jpa.repositories.DonationRequestRepository;
import com.fluxusbackend.shrinkage.domain.model.enums.ShrinkageStatus;
import com.fluxusbackend.shrinkage.domain.model.events.ShrinkageStatusChangedEvent;
import com.fluxusbackend.shrinkage.domain.model.valueobjects.ShrinkageId;
import com.fluxusbackend.shrinkage.infrastructure.persistence.jpa.repositories.ShrinkageRepository;

import java.time.Instant;
import java.time.LocalDate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonationRequestCommandServiceImpl implements DonationRequestCommandService {

    private final DonationRequestRepository repository;
    private final ShrinkageRepository shrinkageRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DonationRequestCommandServiceImpl(
            DonationRequestRepository repository,
            ShrinkageRepository shrinkageRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.shrinkageRepository = shrinkageRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public DonationRequest handle(CreateDonationRequestCommand command) {
        var shrinkageRef = new ShrinkageReferenceId(command.mermaId());
        var benefRef = new BeneficiaryReferenceId(command.beneficiaryId());
        var shrinkage = shrinkageRepository.findById(shrinkageRef.value())
                .orElseThrow(() -> new IllegalArgumentException("Shrinkage not found"));
        if (shrinkage.getStatus() != ShrinkageStatus.DONABLE) {
            throw new IllegalStateException("Only donable shrinkages can receive requests");
        }
        var companyId = shrinkage.getCompanyId()
            .orElseThrow(() -> new IllegalArgumentException("Shrinkage does not have an associated company"));
        var request = new DonationRequest(shrinkageRef, benefRef, companyId, command.notes());
        return repository.save(request);
    }

    @Override
    @Transactional
    public DonationRequest handle(AcceptDonationRequestCommand command) {
        var request = repository.findById(command.requestId().value())
            .orElseThrow(() -> new IllegalArgumentException("Donation request not found"));
        request.accept();
        repository.save(request);
        updateShrinkageToInProcess(request.getShrinkageReferenceId().value());
        return request;
    }

    @Override
    @Transactional
    public DonationRequest handle(RejectDonationRequestCommand command) {
        var request = repository.findById(command.requestId().value())
            .orElseThrow(() -> new IllegalArgumentException("Donation request not found"));
        request.reject();
        repository.save(request);
        updateShrinkageToInProcess(request.getShrinkageReferenceId().value());
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

    private void updateShrinkageToInProcess(Long shrinkageId) {
        var shrinkage = shrinkageRepository.findById(shrinkageId)
                .orElseThrow(() -> new IllegalArgumentException("Shrinkage not found"));
        if (shrinkage.getStatus() == ShrinkageStatus.DONABLE) {
            var oldStatus = shrinkage.getStatus();
            shrinkage.markInProcess();
            var saved = shrinkageRepository.save(shrinkage);
            eventPublisher.publishEvent(new ShrinkageStatusChangedEvent(new ShrinkageId(saved.getShrinkageId()), oldStatus, saved.getStatus(), Instant.now()));
        }
    }

}

