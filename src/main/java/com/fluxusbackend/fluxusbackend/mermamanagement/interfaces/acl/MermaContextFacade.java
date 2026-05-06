package com.fluxusbackend.fluxusbackend.mermamanagement.interfaces.acl;

public interface MermaContextFacade {
    Long findMermaIdById(Long mermaId);

    Long findCompanyIdByMermaId(Long mermaId);

    boolean markMermaDonated(Long mermaId);
}

