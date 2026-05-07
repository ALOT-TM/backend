package com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.dto;

import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.aggregates.UserAccount;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.enums.UserRole;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.enums.UserStatus;
import com.fluxusbackend.fluxusbackend.shared.domain.model.valueobjects.CompanyId;

public class UserAccountDto {

    private final Long id;
    private final String email;
    private final UserRole role;
    private final UserStatus status;
    private final Long companyId;

    public UserAccountDto(Long id, String email, UserRole role, UserStatus status, Long companyId) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.status = status;
        this.companyId = companyId;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public static UserAccountDto from(UserAccount user) {
        java.util.Optional<CompanyId> cid = user.getCompanyId();
        Long companyLong = cid.map(CompanyId::value).orElse(null);
        return new UserAccountDto(user.getUserId().value(), user.getEmail().value(), user.getRole(), user.getStatus(), companyLong);
    }
}
