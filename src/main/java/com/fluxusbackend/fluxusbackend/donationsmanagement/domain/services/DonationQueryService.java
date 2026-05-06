package com.fluxusbackend.fluxusbackend.donationsmanagement.domain.services;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.aggregates.Donation;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.GetDonationByIdQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.ListDonationsByBeneficiaryQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.ListDonationsByStatusQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.ListDonationStatisticsQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.interfaces.rest.dto.DonationStatisticDto;
import java.util.List;
import java.util.Optional;

public interface DonationQueryService {
    Optional<Donation> handle(GetDonationByIdQuery query);

    List<Donation> handle(ListDonationsByStatusQuery query);

    List<Donation> handle(ListDonationsByBeneficiaryQuery query);
    List<DonationStatisticDto> handle(ListDonationStatisticsQuery query);
}

