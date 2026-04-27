package com.fluxusbackend.fluxusbackend.donationsmanagement.application.internal.commandservices;

import com.fluxusbackend.fluxusbackend.donationsmanagement.application.internal.outboundservices.acl.ExternalBeneficiaryService;
import com.fluxusbackend.fluxusbackend.donationsmanagement.application.internal.outboundservices.acl.ExternalMermaService;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.aggregates.Donation;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.ConfirmDonationReceptionCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.CreateDonationCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.MarkDonationDeliveredCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.services.DonationCommandService;
import com.fluxusbackend.fluxusbackend.donationsmanagement.infrastructure.persistence.jpa.repositories.DonationRepository;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class DonationCommandServiceImpl implements DonationCommandService {

    private final DonationRepository repository;
    private final ExternalMermaService externalMermaService;
    private final ExternalBeneficiaryService externalBeneficiaryService;

    public DonationCommandServiceImpl(
            DonationRepository repository,
            ExternalMermaService externalMermaService,
            ExternalBeneficiaryService externalBeneficiaryService
    ) {
        this.repository = repository;
        this.externalMermaService = externalMermaService;
        this.externalBeneficiaryService = externalBeneficiaryService;
    }

    @Override
    @Transactional
    public Donation handle(CreateDonationCommand command) {
        var merma = externalMermaService.fetchMermaById(command.mermaReferenceId().value())
                .orElseThrow(() -> new NoSuchElementException("Merma not found"));
        var beneficiary = externalBeneficiaryService.fetchBeneficiaryById(command.beneficiaryReferenceId().value())
                .orElseThrow(() -> new NoSuchElementException("Beneficiary not found"));

        var donation = new Donation(
                merma,
                beneficiary,
                command.quantity(),
                command.scheduledDeliveryDate()
        );
        return repository.save(donation);
    }

    @Override
    @Transactional
    public Donation handle(MarkDonationDeliveredCommand command) {
        var donation = repository.findById(command.donationId().value())
                .orElseThrow(() -> new NoSuchElementException("Donation not found"));
        donation.markDelivered(command.deliveryDate());
        return repository.save(donation);
    }

    @Override
    @Transactional
    public Donation handle(ConfirmDonationReceptionCommand command) {
        var donation = repository.findById(command.donationId().value())
                .orElseThrow(() -> new NoSuchElementException("Donation not found"));
        donation.confirmReception(command.receptionDate(), command.comment());
        repository.save(donation);
        var updated = externalMermaService.markMermaDonated(donation.getMermaReferenceId().value());
        if (!updated) {
            throw new IllegalStateException("Unable to mark merma as donated");
        }
        return donation;
    }
}

