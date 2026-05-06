package com.fluxusbackend.fluxusbackend.donationsmanagement.application.internal.outboundservices.acl;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.MermaReferenceId;
import com.fluxusbackend.fluxusbackend.mermamanagement.interfaces.acl.MermaContextFacade;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ExternalMermaService {

    private final MermaContextFacade mermaContextFacade;

    public ExternalMermaService(MermaContextFacade mermaContextFacade) {
        this.mermaContextFacade = mermaContextFacade;
    }

    public Optional<MermaReferenceId> fetchMermaById(Long mermaId) {
        var id = mermaContextFacade.findMermaIdById(mermaId);
        return id == 0L ? Optional.empty() : Optional.of(new MermaReferenceId(id));
    }

    public Optional<Long> fetchMermaCompanyId(Long mermaId) {
        var companyId = mermaContextFacade.findCompanyIdByMermaId(mermaId);
        return companyId == null || companyId == 0L ? Optional.empty() : Optional.of(companyId);
    }

    public boolean markMermaDonated(Long mermaId) {
        return mermaContextFacade.markMermaDonated(mermaId);
    }
}

