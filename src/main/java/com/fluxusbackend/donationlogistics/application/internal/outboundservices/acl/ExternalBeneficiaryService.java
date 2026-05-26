package com.fluxusbackend.donationlogistics.application.internal.outboundservices.acl;

import com.fluxusbackend.beneficiary.interfaces.acl.BeneficiariesContextFacade;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.BeneficiaryReferenceId;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ExternalBeneficiaryService {

    private final BeneficiariesContextFacade beneficiariesContextFacade;

    public ExternalBeneficiaryService(BeneficiariesContextFacade beneficiariesContextFacade) {
        this.beneficiariesContextFacade = beneficiariesContextFacade;
    }

    public Optional<BeneficiaryReferenceId> fetchBeneficiaryById(Long beneficiaryId) {
        var id = beneficiariesContextFacade.findBeneficiaryIdById(beneficiaryId);
        return id == 0L ? Optional.empty() : Optional.of(new BeneficiaryReferenceId(id));
    }
}


