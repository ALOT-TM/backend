package com.fluxusbackend.donationlogistics.domain.services;

import com.fluxusbackend.donationlogistics.domain.model.aggregates.Donation;
import com.fluxusbackend.donationlogistics.domain.model.queries.GetDonationByIdQuery;
import com.fluxusbackend.donationlogistics.domain.model.queries.ListDonationsByBeneficiaryQuery;
import com.fluxusbackend.donationlogistics.domain.model.queries.ListDonationsByCompanyQuery;
import com.fluxusbackend.donationlogistics.domain.model.queries.ListDonationsByStatusQuery;
import com.fluxusbackend.donationlogistics.domain.model.queries.ListDonationStatisticsQuery;
import com.fluxusbackend.donationlogistics.interfaces.rest.dto.DonationStatisticDto;
import java.util.List;
import java.util.Optional;

public interface DonationQueryService {
    Optional<Donation> handle(GetDonationByIdQuery query);

    List<Donation> handle(ListDonationsByStatusQuery query);

    List<Donation> handle(ListDonationsByBeneficiaryQuery query);
    List<DonationStatisticDto> handle(ListDonationStatisticsQuery query);
    List<Donation> handle(ListDonationsByCompanyQuery query);
}


