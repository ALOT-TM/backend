package com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.commands;

import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.enums.BeneficiaryType;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.valueobjects.AcceptedProduct;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.valueobjects.Address;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.valueobjects.BeneficiaryId;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.valueobjects.BeneficiaryName;
import java.util.Objects;
import java.util.Set;

public record UpdateBeneficiaryInfoCommand(
        BeneficiaryId beneficiaryId,
        BeneficiaryName name,
        BeneficiaryType type,
        Address address,
        Set<AcceptedProduct> acceptedProducts
) {
    public UpdateBeneficiaryInfoCommand {
        Objects.requireNonNull(beneficiaryId, "Beneficiary id is required");
        Objects.requireNonNull(name, "Name is required");
        Objects.requireNonNull(type, "Type is required");
        Objects.requireNonNull(address, "Address is required");
        Objects.requireNonNull(acceptedProducts, "Accepted products are required");
        if (acceptedProducts.isEmpty()) {
            throw new IllegalArgumentException("At least one accepted product is required");
        }
    }
}

