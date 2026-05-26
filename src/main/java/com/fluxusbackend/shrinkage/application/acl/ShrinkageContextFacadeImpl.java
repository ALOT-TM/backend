package com.fluxusbackend.shrinkage.application.acl;

import com.fluxusbackend.shrinkage.domain.model.commands.MarkShrinkageDonatedCommand;
import com.fluxusbackend.shrinkage.domain.model.queries.GetShrinkageByIdQuery;
import com.fluxusbackend.shrinkage.domain.model.valueobjects.ShrinkageId;
import com.fluxusbackend.shrinkage.domain.services.ShrinkageCommandService;
import com.fluxusbackend.shrinkage.domain.services.ShrinkageQueryService;
import com.fluxusbackend.shrinkage.interfaces.acl.MermaContextFacade;
import org.springframework.stereotype.Service;

@Service
public class ShrinkageContextFacadeImpl implements MermaContextFacade {

    private final ShrinkageCommandService mermaCommandService;
    private final ShrinkageQueryService mermaQueryService;

    public ShrinkageContextFacadeImpl(ShrinkageCommandService mermaCommandService, ShrinkageQueryService mermaQueryService) {
        this.mermaCommandService = mermaCommandService;
        this.mermaQueryService = mermaQueryService;
    }

    @Override
    public Long findMermaIdById(Long mermaId) {
        var query = new GetShrinkageByIdQuery(new ShrinkageId(mermaId));
        var Shrinkage = mermaQueryService.handle(query);
        return Shrinkage.map(value -> value.getShrinkageId().longValue()).orElse(0L);
    }

    @Override
    public Long findCompanyIdByMermaId(Long mermaId) {
        var query = new GetShrinkageByIdQuery(new ShrinkageId(mermaId));
        var Shrinkage = mermaQueryService.handle(query);
        return Shrinkage.flatMap(m -> m.getCompanyId().map(cid -> cid.value())).orElse(0L);
    }

    @Override
    public boolean markMermaDonated(Long mermaId) {
        var query = new GetShrinkageByIdQuery(new ShrinkageId(mermaId));
        var Shrinkage = mermaQueryService.handle(query);
        if (Shrinkage.isEmpty()) {
            return false;
        }
        mermaCommandService.handle(new MarkShrinkageDonatedCommand(new ShrinkageId(mermaId)));
        return true;
    }
}


