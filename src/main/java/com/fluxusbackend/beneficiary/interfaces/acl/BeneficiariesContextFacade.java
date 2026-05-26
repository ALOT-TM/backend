package com.fluxusbackend.beneficiary.interfaces.acl;

public interface BeneficiariesContextFacade {
    Long findBeneficiaryIdById(Long beneficiaryId);
    String findBeneficiaryNameById(Long beneficiaryId);
}


