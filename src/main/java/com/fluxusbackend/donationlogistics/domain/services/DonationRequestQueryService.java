package com.fluxusbackend.donationlogistics.domain.services;

import com.fluxusbackend.donationlogistics.domain.model.aggregates.DonationRequest;
import com.fluxusbackend.donationlogistics.domain.model.queries.GetDonationRequestByIdQuery;
import com.fluxusbackend.donationlogistics.domain.model.queries.ListDonationRequestsByBeneficiaryQuery;
import com.fluxusbackend.donationlogistics.domain.model.queries.ListDonationRequestsByShrinkageQuery;
import com.fluxusbackend.donationlogistics.domain.model.queries.ListDonationRequestsByCompanyQuery;
import com.fluxusbackend.donationlogistics.domain.model.queries.ListDonationRequestsByProductNameQuery;
import java.util.List;
import java.util.Optional;

public interface DonationRequestQueryService {
    Optional<DonationRequest> handle(GetDonationRequestByIdQuery query);

    List<DonationRequest> handle(ListDonationRequestsByBeneficiaryQuery query);

    List<DonationRequest> handle(ListDonationRequestsByShrinkageQuery query);
    List<DonationRequest> handle(ListDonationRequestsByCompanyQuery query);
    List<DonationRequest> handle(ListDonationRequestsByProductNameQuery query);
}

