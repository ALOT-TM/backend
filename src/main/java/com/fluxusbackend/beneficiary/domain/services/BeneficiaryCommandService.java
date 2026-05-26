package com.fluxusbackend.beneficiary.domain.services;

import com.fluxusbackend.beneficiary.domain.model.aggregates.BeneficiaryInstitution;
import com.fluxusbackend.beneficiary.domain.model.commands.RegisterBeneficiaryCommand;
import com.fluxusbackend.beneficiary.domain.model.commands.UpdateBeneficiaryInfoCommand;

public interface BeneficiaryCommandService {
    BeneficiaryInstitution handle(RegisterBeneficiaryCommand command);

    BeneficiaryInstitution handle(UpdateBeneficiaryInfoCommand command);
}


