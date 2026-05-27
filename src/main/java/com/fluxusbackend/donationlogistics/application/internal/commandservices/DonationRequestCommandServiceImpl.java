package com.fluxusbackend.donationlogistics.application.internal.commandservices;

import com.fluxusbackend.donationlogistics.domain.model.aggregates.DonationRequest;
import com.fluxusbackend.donationlogistics.domain.model.commands.AcceptDonationRequestCommand;
import com.fluxusbackend.donationlogistics.domain.model.commands.CancelDonationRequestCommand;
import com.fluxusbackend.donationlogistics.domain.model.commands.CreateDonationRequestCommand;

import com.fluxusbackend.donationlogistics.domain.model.commands.RejectDonationRequestCommand;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.BeneficiaryReferenceId;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.DonationQuantity;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.MermaReferenceId;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.ScheduledDeliveryDate;
import com.fluxusbackend.donationlogistics.domain.services.DonationCommandService;
import com.fluxusbackend.donationlogistics.domain.services.DonationRequestCommandService;
import com.fluxusbackend.donationlogistics.infrastructure.persistence.jpa.repositories.DonationRequestRepository;
import com.fluxusbackend.shrinkage.domain.model.enums.ShrinkageStatus;
import com.fluxusbackend.shrinkage.infrastructure.persistence.jpa.repositories.ShrinkageRepository;
import com.fluxusbackend.shared.application.audit.StatusChangeLogService;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonationRequestCommandServiceImpl implements DonationRequestCommandService {

    private final DonationRequestRepository repository;
    private final ShrinkageRepository mermaRepository;
    private final StatusChangeLogService statusChangeLogService;

    public DonationRequestCommandServiceImpl(
            DonationRequestRepository repository,
            ShrinkageRepository mermaRepository,
            StatusChangeLogService statusChangeLogService
    ) {
        this.repository = repository;
        this.mermaRepository = mermaRepository;
        this.statusChangeLogService = statusChangeLogService;
    }

    @Override
    @Transactional
    public DonationRequest handle(CreateDonationRequestCommand command) {
        var mermaRef = new MermaReferenceId(command.mermaId());
        var benefRef = new BeneficiaryReferenceId(command.beneficiaryId());
        var merma = mermaRepository.findById(mermaRef.value())
                .orElseThrow(() -> new IllegalArgumentException("Merma not found"));
        if (merma.getStatus() != ShrinkageStatus.DONABLE) {
            throw new IllegalStateException("Only donable mermas can receive requests");
        }
        var companyId = merma.getCompanyId()
            .orElseThrow(() -> new IllegalArgumentException("Merma does not have an associated company"));
        var request = new DonationRequest(mermaRef, benefRef, companyId, command.notes());
        var saved = repository.save(request);
        statusChangeLogService.recordChange(
                "DONATION_REQUEST",
                saved.getDonationRequestId().value(),
                null,
                saved.getStatus().name()
        );
        return saved;
    }

    @Override
    @Transactional
    public DonationRequest handle(AcceptDonationRequestCommand command) {
        var request = repository.findById(command.requestId().value())
            .orElseThrow(() -> new IllegalArgumentException("Donation request not found"));
        var fromStatus = request.getStatus();
        request.accept();
        repository.save(request);
        updateMermaToInProcess(request.getMermaReferenceId().value());
        statusChangeLogService.recordChange(
                "DONATION_REQUEST",
                request.getDonationRequestId().value(),
                fromStatus.name(),
                request.getStatus().name()
        );
        return request;
    }

    @Override
    @Transactional
    public DonationRequest handle(RejectDonationRequestCommand command) {
        var request = repository.findById(command.requestId().value())
            .orElseThrow(() -> new IllegalArgumentException("Donation request not found"));
        var fromStatus = request.getStatus();
        request.reject();
        repository.save(request);
        updateMermaToInProcess(request.getMermaReferenceId().value());
        statusChangeLogService.recordChange(
                "DONATION_REQUEST",
                request.getDonationRequestId().value(),
                fromStatus.name(),
                request.getStatus().name()
        );
        return request;
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

    private void updateMermaToInProcess(Long mermaId) {
        var merma = mermaRepository.findById(mermaId)
                .orElseThrow(() -> new IllegalArgumentException("Merma not found"));
        if (merma.getStatus() == ShrinkageStatus.DONABLE) {
            var fromStatus = merma.getStatus();
            merma.markInProcess();
            var saved = mermaRepository.save(merma);
            statusChangeLogService.recordChange(
                    "SHRINKAGE",
                    saved.getShrinkageId(),
                    fromStatus.name(),
                    saved.getStatus().name()
            );
        }
    }

}

