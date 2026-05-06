package com.fluxusbackend.fluxusbackend.mermamanagement.domain.services;

import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.aggregates.Merma;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.queries.GetMermaByIdQuery;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.queries.ListMermasByStatusQuery;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.queries.ListMermasByCompanyQuery;
import java.util.List;
import java.util.Optional;

public interface MermaQueryService {
    Optional<Merma> handle(GetMermaByIdQuery query);

    List<Merma> handle(ListMermasByStatusQuery query);

    List<Merma> handle(ListMermasByCompanyQuery query);
}

