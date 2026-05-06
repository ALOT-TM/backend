package com.fluxusbackend.fluxusbackend.beneficiariesmanagement.application.acl;

import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.queries.GetBeneficiaryByIdQuery;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.valueobjects.BeneficiaryId;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.services.BeneficiaryQueryService;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.interfaces.acl.BeneficiariesContextFacade;
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
        return beneficiary.map(value -> value.getBeneficiaryId().value()).orElse(0L);
    }

    @Override
    public String findBeneficiaryNameById(Long beneficiaryId) {
        var query = new GetBeneficiaryByIdQuery(new BeneficiaryId(beneficiaryId));
        var beneficiary = beneficiaryQueryService.handle(query);
        return beneficiary.map(value -> value.getName().value()).orElse("Unknown");
    }
}

