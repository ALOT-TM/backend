package com.fluxusbackend.shrinkage.domain.services;

import com.fluxusbackend.shrinkage.domain.model.aggregates.Shrinkage;
import com.fluxusbackend.shrinkage.domain.model.queries.GetShrinkageByIdQuery;
import com.fluxusbackend.shrinkage.domain.model.queries.ListShrinkagesByStatusQuery;
import com.fluxusbackend.shrinkage.domain.model.queries.ListShrinkagesByCompanyQuery;
import java.util.List;
import java.util.Optional;

public interface ShrinkageQueryService {
    Optional<Shrinkage> handle(GetShrinkageByIdQuery query);

    List<Shrinkage> handle(ListShrinkagesByStatusQuery query);

    List<Shrinkage> handle(ListShrinkagesByCompanyQuery query);
}


