package com.fluxusbackend.fluxusbackend.donationsmanagement.domain.services;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.aggregates.DonationRequest;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.GetDonationRequestByIdQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.ListDonationRequestsByBeneficiaryQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.ListDonationRequestsByMermaQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.ListDonationRequestsByCompanyQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.ListDonationRequestsByProductNameQuery;
import java.util.List;
import java.util.Optional;

public interface DonationRequestQueryService {
    Optional<DonationRequest> handle(GetDonationRequestByIdQuery query);

    List<DonationRequest> handle(ListDonationRequestsByBeneficiaryQuery query);

    List<DonationRequest> handle(ListDonationRequestsByMermaQuery query);
    List<DonationRequest> handle(ListDonationRequestsByCompanyQuery query);
    List<DonationRequest> handle(ListDonationRequestsByProductNameQuery query);
}
