package com.fluxusbackend.donationlogistics.application.internal.queryservices;

import com.fluxusbackend.donationlogistics.domain.model.aggregates.Donation;
import com.fluxusbackend.donationlogistics.domain.model.queries.GetDonationByIdQuery;
import com.fluxusbackend.donationlogistics.domain.model.queries.ListDonationsByBeneficiaryQuery;
import com.fluxusbackend.donationlogistics.domain.model.queries.ListDonationsByStatusQuery;
import com.fluxusbackend.donationlogistics.domain.model.queries.ListDonationStatisticsQuery;
import com.fluxusbackend.donationlogistics.interfaces.rest.dto.DonationStatisticDto;
import com.fluxusbackend.beneficiary.interfaces.acl.BeneficiariesContextFacade;
import com.fluxusbackend.donationlogistics.domain.services.DonationQueryService;
import com.fluxusbackend.donationlogistics.infrastructure.persistence.jpa.repositories.DonationRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class DonationQueryServiceImpl implements DonationQueryService {

    private final DonationRepository repository;
    private final BeneficiariesContextFacade beneficiariesContextFacade;

    @Autowired
    public DonationQueryServiceImpl(DonationRepository repository, BeneficiariesContextFacade beneficiariesContextFacade) {
        this.repository = repository;
        this.beneficiariesContextFacade = beneficiariesContextFacade;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Donation> handle(GetDonationByIdQuery query) {
        return repository.findById(query.donationId().value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Donation> handle(ListDonationsByStatusQuery query) {
        return repository.findByStatus(query.status());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Donation> handle(ListDonationsByBeneficiaryQuery query) {
        return repository.findByBeneficiaryReferenceIdValue(query.beneficiaryReferenceId().value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonationStatisticDto> handle(ListDonationStatisticsQuery query) {
        var donations = repository.findByCompanyIdValue(query.companyId().value());
        return donations.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                d -> d.getBeneficiaryReferenceId().value(),
                java.util.stream.Collectors.toList()
            ))
            .entrySet().stream()
            .map(entry -> {
                Long beneficiaryId = entry.getKey();
                List<Donation> beneficiaryDonations = entry.getValue();
                String beneficiaryName = beneficiariesContextFacade.findBeneficiaryNameById(beneficiaryId);
                long totalDonations = beneficiaryDonations.size();
                long totalQuantity = beneficiaryDonations.stream()
                    .mapToLong(d -> d.getQuantity().amount())
                    .sum();
                return new DonationStatisticDto(beneficiaryId, beneficiaryName, totalDonations, totalQuantity);
            })
            .toList();
    }
}


