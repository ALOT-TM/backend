package com.fluxusbackend.shrinkage.application.acl;

import com.fluxusbackend.shrinkage.domain.model.commands.MarkShrinkageDonatedCommand;
import com.fluxusbackend.shrinkage.domain.model.queries.GetShrinkageByIdQuery;
import com.fluxusbackend.shrinkage.domain.model.valueobjects.ShrinkageId;
import com.fluxusbackend.shrinkage.domain.services.ShrinkageCommandService;
import com.fluxusbackend.shrinkage.domain.services.ShrinkageQueryService;
import com.fluxusbackend.shrinkage.interfaces.acl.ShrinkageContextFacade;
import org.springframework.stereotype.Service;

@Service
public class ShrinkageContextFacadeImpl implements ShrinkageContextFacade {

    private final ShrinkageCommandService shrinkageCommandService;
    private final ShrinkageQueryService shrinkageQueryService;

    public ShrinkageContextFacadeImpl(ShrinkageCommandService shrinkageCommandService, ShrinkageQueryService shrinkageQueryService) {
        this.shrinkageCommandService = shrinkageCommandService;
        this.shrinkageQueryService = shrinkageQueryService;
    }

    @Override
    public Long findShrinkageIdById(Long shrinkageId) {
        var query = new GetShrinkageByIdQuery(new ShrinkageId(shrinkageId));
        var shrinkage = shrinkageQueryService.handle(query);
        return shrinkage.map(value -> value.getShrinkageId().longValue()).orElse(0L);
    }

    @Override
    public Long findCompanyIdByShrinkageId(Long shrinkageId) {
        var query = new GetShrinkageByIdQuery(new ShrinkageId(shrinkageId));
        var shrinkage = shrinkageQueryService.handle(query);
        return shrinkage.flatMap(m -> m.getCompanyId().map(cid -> cid.value())).orElse(0L);
    }

    @Override
    public boolean markShrinkageDonated(Long shrinkageId) {
        var query = new GetShrinkageByIdQuery(new ShrinkageId(shrinkageId));
        var shrinkage = shrinkageQueryService.handle(query);
        if (shrinkage.isEmpty()) {
            return false;
        }
        shrinkageCommandService.handle(new MarkShrinkageDonatedCommand(new ShrinkageId(shrinkageId)));
        return true;
    }

    @Override
    public String findShrinkageStatus(Long shrinkageId) {
        var query = new GetShrinkageByIdQuery(new ShrinkageId(shrinkageId));
        var shrinkage = shrinkageQueryService.handle(query);
        return shrinkage.map(value -> value.getStatus().name()).orElse(null);
    }
}


