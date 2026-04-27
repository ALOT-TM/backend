package com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.services;

import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.aggregates.Beneficiary;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.commands.ActivateBeneficiaryCommand;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.commands.DeactivateBeneficiaryCommand;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.commands.RegisterBeneficiaryCommand;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.commands.UpdateBeneficiaryInfoCommand;

public interface BeneficiaryCommandService {
    Beneficiary handle(RegisterBeneficiaryCommand command);

    Beneficiary handle(UpdateBeneficiaryInfoCommand command);

    Beneficiary handle(ActivateBeneficiaryCommand command);

    Beneficiary handle(DeactivateBeneficiaryCommand command);
}

