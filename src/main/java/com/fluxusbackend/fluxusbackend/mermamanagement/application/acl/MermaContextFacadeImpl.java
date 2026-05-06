package com.fluxusbackend.fluxusbackend.mermamanagement.application.acl;

import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.commands.MarkMermaDonatedCommand;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.queries.GetMermaByIdQuery;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.valueobjects.MermaId;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.services.MermaCommandService;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.services.MermaQueryService;
import com.fluxusbackend.fluxusbackend.mermamanagement.interfaces.acl.MermaContextFacade;
import org.springframework.stereotype.Service;

@Service
public class MermaContextFacadeImpl implements MermaContextFacade {

    private final MermaCommandService mermaCommandService;
    private final MermaQueryService mermaQueryService;

    public MermaContextFacadeImpl(MermaCommandService mermaCommandService, MermaQueryService mermaQueryService) {
        this.mermaCommandService = mermaCommandService;
        this.mermaQueryService = mermaQueryService;
    }

    @Override
    public Long findMermaIdById(Long mermaId) {
        var query = new GetMermaByIdQuery(new MermaId(mermaId));
        var merma = mermaQueryService.handle(query);
        return merma.map(value -> value.getMermaId().value()).orElse(0L);
    }

    @Override
    public Long findCompanyIdByMermaId(Long mermaId) {
        var query = new GetMermaByIdQuery(new MermaId(mermaId));
        var merma = mermaQueryService.handle(query);
        return merma.flatMap(m -> m.getCompanyId().map(cid -> cid.value())).orElse(0L);
    }

    @Override
    public boolean markMermaDonated(Long mermaId) {
        var query = new GetMermaByIdQuery(new MermaId(mermaId));
        var merma = mermaQueryService.handle(query);
        if (merma.isEmpty()) {
            return false;
        }
        mermaCommandService.handle(new MarkMermaDonatedCommand(new MermaId(mermaId)));
        return true;
    }
}

