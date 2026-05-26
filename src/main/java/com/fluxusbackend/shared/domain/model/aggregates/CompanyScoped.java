package com.fluxusbackend.shared.domain.model.aggregates;

import com.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
import java.util.Optional;

public interface CompanyScoped {

    Optional<CompanyId> getCompanyId();

    void setCompanyId(CompanyId companyId);

}
