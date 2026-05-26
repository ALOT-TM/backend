package com.fluxusbackend.authaccess.domain.model.dto;

import com.fluxusbackend.authaccess.domain.model.aggregates.UserAccount;
import com.fluxusbackend.authaccess.domain.model.enums.UserActor;

public class UserAccountDto {

    private final Long id;
    private final String email;
    private final String username;
    private final UserActor actor;
    private final Long retailCompanyId;
    private final Long roleId;
    private final Long beneficiaryInstitutionId;
    private final Boolean retailUserActive;

    public UserAccountDto(
            Long id,
            String email,
            String username,
            UserActor actor,
            Long retailCompanyId,
            Long roleId,
            Long beneficiaryInstitutionId,
            Boolean retailUserActive
    ) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.actor = actor;
        this.retailCompanyId = retailCompanyId;
        this.roleId = roleId;
        this.beneficiaryInstitutionId = beneficiaryInstitutionId;
        this.retailUserActive = retailUserActive;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public UserActor getActor() {
        return actor;
    }

    public Long getRetailCompanyId() {
        return retailCompanyId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public Long getBeneficiaryInstitutionId() {
        return beneficiaryInstitutionId;
    }

    public Boolean getRetailUserActive() {
        return retailUserActive;
    }

    public static UserAccountDto from(UserAccount user) {
        var retail = user.getRetailUser().orElse(null);
        var beneficiary = user.getBeneficiaryUser().orElse(null);
        var actor = retail != null ? UserActor.RETAIL : UserActor.BENEFICIARY;
        Long retailCompanyId = retail == null ? null : retail.getRetailCompany().getRetailCompanyId();
        Long roleId = retail == null ? null : retail.getRole().getRoleId();
        Long beneficiaryInstitutionId = beneficiary == null ? null : beneficiary.getBeneficiaryInstitution().getBeneficiaryInstitutionId();
        Boolean retailUserActive = retail == null ? null : retail.isActive();
        return new UserAccountDto(
            user.getUserId().value(),
            user.getEmail().value(),
            user.getUsername(),
            actor,
            retailCompanyId,
            roleId,
            beneficiaryInstitutionId,
            retailUserActive
        );
    }
}

