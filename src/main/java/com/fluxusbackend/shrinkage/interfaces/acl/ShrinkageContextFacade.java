package com.fluxusbackend.shrinkage.interfaces.acl;

public interface ShrinkageContextFacade {
    Long findShrinkageIdById(Long shrinkageId);

    Long findCompanyIdByShrinkageId(Long shrinkageId);

    boolean markShrinkageDonated(Long shrinkageId);

    String findShrinkageStatus(Long shrinkageId);
}
