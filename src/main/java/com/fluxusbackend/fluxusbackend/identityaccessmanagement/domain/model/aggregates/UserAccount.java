package com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.aggregates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.enums.UserRole;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.enums.UserStatus;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.valueobjects.EmailAddress;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.valueobjects.PasswordHash;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.valueobjects.UserId;
import com.fluxusbackend.fluxusbackend.shared.domain.model.aggregates.AuditableAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "user_accounts")
public class UserAccount extends AuditableAggregateRoot {

    @Embedded
    private EmailAddress email;

    @Embedded
    private PasswordHash passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 40)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;

    protected UserAccount() {
    }

    public UserAccount(EmailAddress email, PasswordHash passwordHash, UserRole role) {
        this.email = Objects.requireNonNull(email, "Email is required");
        this.passwordHash = Objects.requireNonNull(passwordHash, "Password hash is required");
        this.role = Objects.requireNonNull(role, "Role is required");
        this.status = UserStatus.ACTIVE;
    }

    public UserId getUserId() {
        return new UserId(getId());
    }

    public EmailAddress getEmail() {
        return email;
    }

    @JsonIgnore
    public PasswordHash getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }
}

