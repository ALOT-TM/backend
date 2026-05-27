package com.fluxusbackend.donationlogistics.application.internal.outboundservices.acl;

import com.fluxusbackend.donationlogistics.domain.model.valueobjects.ShrinkageReferenceId;
import com.fluxusbackend.shrinkage.interfaces.acl.ShrinkageContextFacade;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ExternalShrinkageService {

    private final ShrinkageContextFacade shrinkageContextFacade;

    public ExternalShrinkageService(ShrinkageContextFacade shrinkageContextFacade) {
        this.shrinkageContextFacade = shrinkageContextFacade;
    }

    public Optional<ShrinkageReferenceId> fetchShrinkageById(Long shrinkageId) {
        var id = shrinkageContextFacade.findShrinkageIdById(shrinkageId);
        return id == 0L ? Optional.empty() : Optional.of(new ShrinkageReferenceId(id));
    }

    public Optional<Long> fetchShrinkageCompanyId(Long shrinkageId) {
        var companyId = shrinkageContextFacade.findCompanyIdByShrinkageId(shrinkageId);
        return companyId == null || companyId == 0L ? Optional.empty() : Optional.of(companyId);
    }

    public boolean markShrinkageDonated(Long shrinkageId) {
        return shrinkageContextFacade.markShrinkageDonated(shrinkageId);
    }
}
