package com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.services;

import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.aggregates.Beneficiary;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.queries.GetBeneficiaryByIdQuery;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.queries.ListBeneficiariesByStatusQuery;
import java.util.List;
import java.util.Optional;

public interface BeneficiaryQueryService {
    Optional<Beneficiary> handle(GetBeneficiaryByIdQuery query);

    List<Beneficiary> handle(ListBeneficiariesByStatusQuery query);
}

