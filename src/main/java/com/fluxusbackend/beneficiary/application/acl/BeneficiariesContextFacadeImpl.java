package com.fluxusbackend.beneficiary.application.acl;

import com.fluxusbackend.beneficiary.domain.model.queries.GetBeneficiaryByIdQuery;
import com.fluxusbackend.beneficiary.domain.model.valueobjects.BeneficiaryId;
import com.fluxusbackend.beneficiary.domain.services.BeneficiaryQueryService;
import com.fluxusbackend.beneficiary.interfaces.acl.BeneficiariesContextFacade;
import org.springframework.stereotype.Service;

@Service
public class BeneficiariesContextFacadeImpl implements BeneficiariesContextFacade {

    private final BeneficiaryQueryService beneficiaryQueryService;

    public BeneficiariesContextFacadeImpl(BeneficiaryQueryService beneficiaryQueryService) {
        this.beneficiaryQueryService = beneficiaryQueryService;
    }

    @Override
    public Long findBeneficiaryIdById(Long beneficiaryId) {
        var query = new GetBeneficiaryByIdQuery(new BeneficiaryId(beneficiaryId));
        var beneficiary = beneficiaryQueryService.handle(query);
        return beneficiary.map(value -> value.getBeneficiaryInstitutionId()).orElse(0L);
    }

    @Override
    public String findBeneficiaryNameById(Long beneficiaryId) {
        var query = new GetBeneficiaryByIdQuery(new BeneficiaryId(beneficiaryId));
        var beneficiary = beneficiaryQueryService.handle(query);
        return beneficiary.map(value -> value.getName()).orElse("Unknown");
    }
}


