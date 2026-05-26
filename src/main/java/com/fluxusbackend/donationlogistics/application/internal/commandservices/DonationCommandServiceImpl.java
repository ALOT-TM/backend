package com.fluxusbackend.donationlogistics.application.internal.commandservices;

import com.fluxusbackend.donationlogistics.application.internal.outboundservices.acl.ExternalBeneficiaryService;
import com.fluxusbackend.donationlogistics.application.internal.outboundservices.acl.ExternalMermaService;
import com.fluxusbackend.donationlogistics.domain.model.aggregates.Donation;
import com.fluxusbackend.donationlogistics.domain.model.commands.ConfirmDonationReceptionCommand;
import com.fluxusbackend.donationlogistics.domain.model.commands.CreateDonationCommand;
import com.fluxusbackend.donationlogistics.domain.model.commands.MarkDonationDeliveredCommand;
import com.fluxusbackend.donationlogistics.domain.services.DonationCommandService;
import com.fluxusbackend.donationlogistics.infrastructure.persistence.jpa.repositories.DonationRepository;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class DonationCommandServiceImpl implements DonationCommandService {

    private final DonationRepository repository;
    private final ExternalMermaService externalMermaService;
    private final ExternalBeneficiaryService externalBeneficiaryService;
    private final com.fluxusbackend.shared.application.security.AclService aclService;

    public DonationCommandServiceImpl(
            DonationRepository repository,
            ExternalMermaService externalMermaService,
            ExternalBeneficiaryService externalBeneficiaryService,
            com.fluxusbackend.shared.application.security.AclService aclService
    ) {
        this.repository = repository;
        this.externalMermaService = externalMermaService;
        this.externalBeneficiaryService = externalBeneficiaryService;
        this.aclService = aclService;
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
        // require retail user and set company id on donation to match merma
        var companyId = aclService.requireRetailCompanyForCreate();
        // verify merma belongs to same company
        var mermaCompany = externalMermaService.fetchMermaCompanyId(merma.value());
        if (mermaCompany.isEmpty() || !mermaCompany.get().equals(companyId.value())) {
            throw new SecurityException("Merma does not belong to the current user's company");
        }
        donation.setCompanyId(companyId);
        return repository.save(donation);
    }

    @Override
    @Transactional
    public Donation handle(MarkDonationDeliveredCommand command) {
        var donation = repository.findById(command.donationId().value())
                .orElseThrow(() -> new NoSuchElementException("Donation not found"));
        aclService.ensureSameCompanyForRetail(donation);
        donation.markDelivered(command.deliveryDate());
        return repository.save(donation);
    }

    @Override
    @Transactional
    public Donation handle(ConfirmDonationReceptionCommand command) {
        var donation = repository.findById(command.donationId().value())
                .orElseThrow(() -> new NoSuchElementException("Donation not found"));
        aclService.ensureSameCompanyForRetail(donation);
        donation.confirmReception(command.receptionDate(), command.comment());
        repository.save(donation);
        var updated = externalMermaService.markMermaDonated(donation.getMermaReferenceId().value());
        if (!updated) {
            throw new IllegalStateException("Unable to mark merma as donated");
        }
        return donation;
    }
}


