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
import com.fluxusbackend.shared.application.audit.StatusChangeLogService;

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
    private final StatusChangeLogService statusChangeLogService;
    public DonationRequestCommandServiceImpl(
            DonationRequestRepository repository,
            ShrinkageRepository shrinkageRepository,
            ApplicationEventPublisher eventPublisher,
            StatusChangeLogService statusChangeLogService
    ) {
        this.repository = repository;
        this.shrinkageRepository = shrinkageRepository;
        this.eventPublisher = eventPublisher;
        this.statusChangeLogService = statusChangeLogService;
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
        var fromStatus = request.getStatus();
        request.accept();
        var saved = repository.save(request);
        statusChangeLogService.recordChange(
                "DONATION_REQUEST",
                saved.getDonationRequestId().value(),
                fromStatus.name(),
                saved.getStatus().name()
        );
        updateShrinkageToRequested(request.getShrinkageReferenceId().value());
        return saved;
    }

    @Override
    @Transactional
    public DonationRequest handle(RejectDonationRequestCommand command) {
        var request = repository.findById(command.requestId().value())
            .orElseThrow(() -> new IllegalArgumentException("Donation request not found"));
        var fromStatus = request.getStatus();
        request.reject();
        var saved = repository.save(request);
        statusChangeLogService.recordChange(
                "DONATION_REQUEST",
                saved.getDonationRequestId().value(),
                fromStatus.name(),
                saved.getStatus().name()
        );
        updateShrinkageToRequested(request.getShrinkageReferenceId().value());
        return saved;
    }

    @Override
    @Transactional
    public DonationRequest handle(CancelDonationRequestCommand command) {
        var request = repository.findById(command.requestId().value())
            .orElseThrow(() -> new IllegalArgumentException("Donation request not found"));
        var fromStatus = request.getStatus();
        request.cancel();
        var saved = repository.save(request);
        statusChangeLogService.recordChange(
                "DONATION_REQUEST",
                saved.getDonationRequestId().value(),
                fromStatus.name(),
                saved.getStatus().name()
        );
        return saved;
    }

        private void updateShrinkageToRequested(Long shrinkageId) {
        var shrinkage = shrinkageRepository.findById(shrinkageId)
                .orElseThrow(() -> new IllegalArgumentException("Shrinkage not found"));
        if (shrinkage.getStatus() == ShrinkageStatus.DONABLE) {
            var oldStatus = shrinkage.getStatus();
            shrinkage.markRequested();
            var saved = shrinkageRepository.save(shrinkage);
            statusChangeLogService.recordChange(
                "SHRINKAGE",
                saved.getShrinkageId(),
                oldStatus.name(),
                saved.getStatus().name()
            );
            eventPublisher.publishEvent(new ShrinkageStatusChangedEvent(new ShrinkageId(saved.getShrinkageId()), oldStatus, saved.getStatus(), Instant.now()));
        }
    }

}

