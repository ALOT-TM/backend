package com.fluxusbackend.beneficiary.domain.services;

import com.fluxusbackend.beneficiary.domain.model.aggregates.BeneficiaryInstitution;
import com.fluxusbackend.beneficiary.domain.model.queries.GetBeneficiaryByIdQuery;
import com.fluxusbackend.beneficiary.domain.model.queries.ListBeneficiaryInstitutionsQuery;
import java.util.List;
import java.util.Optional;

public interface BeneficiaryQueryService {
    Optional<BeneficiaryInstitution> handle(GetBeneficiaryByIdQuery query);

    List<BeneficiaryInstitution> handle(ListBeneficiaryInstitutionsQuery query);
}


